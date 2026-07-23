package com.hrm.service.admin;

import com.hrm.dto.request.ResetSysUserPasswordRequest;
import com.hrm.dto.response.EmployeeResponse;
import com.hrm.dto.response.SysUserResponse;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.BusinessException;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.persistence.executor.SqlExecutor;
import com.hrm.persistence.entity.Employee;
import com.hrm.persistence.entity.Role;
import com.hrm.persistence.entity.SysUser;
import com.hrm.persistence.entity.UserRole;
import com.hrm.persistence.entity.enums.ActiveStatus;
import com.hrm.persistence.mapper.EmployeeMapper;
import com.hrm.persistence.mapper.RoleMapper;
import com.hrm.persistence.mapper.SysUserMapper;
import com.hrm.persistence.mapper.UserRoleMapper;
import com.hrm.service.common.SecurityService;
import com.hrm.utility.Paging;

import java.util.Collections;
import java.util.List;

public class SysUserAdminService {

    private static final int MIN_PASSWORD_LENGTH = 6;

    private final SecurityService securityService = new SecurityService();

    public List<SysUserResponse> getList(UserSession admin, Paging paging, Integer statusFilter, String keyword) {
        assertAdmin(admin);
        String kw = keyword == null || keyword.isBlank() ? null : keyword.trim();
        int total = SqlExecutor.execute(SysUserMapper.class,
                m -> m.countAdminList(statusFilter, kw));
        paging.setSize(total);
        paging.calc();
        if (total == 0) {
            return Collections.emptyList();
        }
        return SqlExecutor.execute(SysUserMapper.class,
                m -> m.selectAdminList(statusFilter, kw, paging.getStart(), paging.getNrpp()));
    }

    public void updateStatus(UserSession admin, Long userId, int statusCode) {
        assertAdmin(admin);
        if (userId == null) {
            throw new ValidationException("Thiếu mã tài khoản");
        }
        ActiveStatus next = ActiveStatus.fromCode(statusCode);
        if (next == null) {
            throw new ValidationException("Trạng thái không hợp lệ");
        }

        SysUser target = SqlExecutor.execute(SysUserMapper.class,
                m -> m.selectByPrimaryKey(userId.intValue()));
        if (target == null) {
            throw new ValidationException("Không tìm thấy tài khoản");
        }

        if (next == ActiveStatus.INACTIVE
                && admin.getUserId() != null
                && admin.getUserId().equals(userId)) {
            int others = SqlExecutor.execute(SysUserMapper.class,
                    m -> m.countActiveAdminsExcept(userId.intValue()));
            if (others <= 0) {
                throw new ValidationException(
                        "Không thể khóa tài khoản ADMIN duy nhất đang hoạt động");
            }
        }

        SqlExecutor.executeMapper(SysUserMapper.class, m -> {
            m.updateStatus(userId.intValue(), next.toByte());
            return null;
        });
    }

    public void resetPassword(UserSession admin, ResetSysUserPasswordRequest req) {
        assertAdmin(admin);
        if (req == null || req.getUserId() == null) {
            throw new ValidationException("Thiếu mã tài khoản");
        }
        String next = req.getNewPassword();
        String confirm = req.getConfirmPassword();
        if (next == null || next.isBlank()) {
            throw new ValidationException("Vui lòng nhập mật khẩu mới");
        }
        if (next.length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException("Mật khẩu mới phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự");
        }
        if (confirm == null || !next.equals(confirm)) {
            throw new ValidationException("Xác nhận mật khẩu không khớp");
        }

        SysUser target = SqlExecutor.execute(SysUserMapper.class,
                m -> m.selectByPrimaryKey(req.getUserId().intValue()));
        if (target == null) {
            throw new ValidationException("Không tìm thấy tài khoản");
        }

        String hash = securityService.hash(next);
        SqlExecutor.executeMapper(SysUserMapper.class, m -> {
            m.updatePasswordHash(req.getUserId().intValue(), hash);
            return null;
        });
    }

    public List<EmployeeResponse> listEmployeesWithoutUser(UserSession admin) {
        assertAdmin(admin);
        List<EmployeeResponse> rows = SqlExecutor.execute(EmployeeMapper.class,
                EmployeeMapper::selectWithoutSysUser);
        return rows == null ? Collections.emptyList() : rows;
    }

    public void provisionUserForEmployee(UserSession admin, Integer employeeId) {
        assertAdmin(admin);
        if (employeeId == null) {
            throw new ValidationException("Nhân viên không hợp lệ");
        }
        SqlExecutor.executeTransaction(session -> {
            EmployeeMapper empMapper = session.getMapper(EmployeeMapper.class);
            SysUserMapper userMapper = session.getMapper(SysUserMapper.class);
            RoleMapper roleMapper = session.getMapper(RoleMapper.class);
            UserRoleMapper urMapper = session.getMapper(UserRoleMapper.class);

            Employee emp = empMapper.selectByPrimaryKey(employeeId);
            if (emp == null) {
                throw new ValidationException("Nhân viên không tồn tại");
            }
            // đã có user?
            List<EmployeeResponse> orphans = empMapper.selectWithoutSysUser();
            boolean stillOrphan = orphans != null && orphans.stream()
                    .anyMatch(e -> e.getEmployeeId() != null && e.getEmployeeId().intValue() == employeeId);
            if (!stillOrphan) {
                throw new BusinessException("Nhân viên đã có tài khoản");
            }
            if (userMapper.selectByUsername(emp.getEmployeeCode()) != null) {
                throw new BusinessException("Username trùng mã nhân viên đã tồn tại");
            }
            Role userRole = roleMapper.selectByRoleName("USER");
            if (userRole == null) {
                throw new BusinessException("Thiếu role USER trong hệ thống");
            }
            SysUser u = new SysUser();
            u.setUsername(emp.getEmployeeCode());
            u.setPasswordHash(securityService.hash("password"));
            u.setEmployeeId(employeeId);
            u.setStatus(ActiveStatus.ACTIVE.toByte());
            userMapper.insert(u);
            UserRole ur = new UserRole();
            ur.setUserId(u.getUserId());
            ur.setRoleId(userRole.getRoleId());
            urMapper.insert(ur);
            return u.getUserId();
        });
    }

    private void assertAdmin(UserSession admin) {
        if (admin == null || !admin.isAdmin()) {
            throw new UnauthorizedException("Chỉ ADMIN được thực hiện thao tác này");
        }
    }
}
