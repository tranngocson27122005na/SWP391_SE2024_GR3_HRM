package com.hrm.service.common;

import com.hrm.dto.request.ChangePasswordForm;
import com.hrm.dto.request.LoginForm;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.persistence.executor.SqlExecutor;
import com.hrm.persistence.entity.Employee;
import com.hrm.persistence.entity.JobPosition;
import com.hrm.persistence.entity.SysUser;
import com.hrm.persistence.mapper.EmployeeMapper;
import com.hrm.persistence.mapper.JobPositionMapper;
import com.hrm.persistence.mapper.SysUserMapper;
import com.hrm.persistence.mapper.UserRoleMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Authentication: validate credentials and build {@link UserSession}.
 */
public class AuthService {

    private static final byte USER_STATUS_ACTIVE = 1;
    private static final int MIN_PASSWORD_LENGTH = 6;

    private final SecurityService securityService = new SecurityService();

    public UserSession login(LoginForm form) {
        String username = form.getUsername() == null ? null : form.getUsername().trim();
        String password = form.getPassword();

        SysUser user = SqlExecutor.execute(SysUserMapper.class,
                mapper -> mapper.selectByUsername(username));

        if (user == null || user.getStatus() == null || user.getStatus() != USER_STATUS_ACTIVE) {
            throw new ValidationException("Tên đăng nhập hoặc mật khẩu không đúng");
        }

        if (!securityService.matches(password, user.getPasswordHash())) {
            throw new ValidationException("Tên đăng nhập hoặc mật khẩu không đúng");
        }

        List<String> roleNames = SqlExecutor.execute(UserRoleMapper.class,
                mapper -> mapper.selectRoleNamesByUserId(user.getUserId()));

        String realm = resolveSingleRealm(roleNames);

        UserSession session = new UserSession();
        session.setUserId(user.getUserId() == null ? null : user.getUserId().longValue());
        session.setUsername(user.getUsername());
        session.setRoles(Set.of(realm));

        if ("ADMIN".equals(realm)) {
            if (user.getEmployeeId() != null) {
                throw new ValidationException("Tài khoản ADMIN không hợp lệ");
            }
            return session;
        }

        // USER realm
        if (user.getEmployeeId() == null) {
            throw new ValidationException("Tài khoản USER chưa gắn nhân viên");
        }
        Employee emp = SqlExecutor.execute(EmployeeMapper.class,
                m -> m.selectByPrimaryKey(user.getEmployeeId()));
        if (emp == null || emp.getPositionId() == null) {
            throw new ValidationException("Tài khoản USER thiếu chức vụ");
        }
        JobPosition jp = SqlExecutor.execute(JobPositionMapper.class,
                m -> m.selectByPrimaryKey(emp.getPositionId()));
        if (jp == null || jp.getDataScope() == null) {
            throw new ValidationException("Tài khoản USER thiếu phạm vi dữ liệu");
        }

        session.setEmployeeId(emp.getEmployeeId().longValue());
        session.setPositionId(jp.getPositionId().longValue());
        session.setDepartmentId(jp.getDepartmentId() == null ? null : jp.getDepartmentId().longValue());
        session.setDataScope(jp.getDataScope().intValue());
        return session;
    }

    public void changePassword(UserSession user, ChangePasswordForm form) {
        if (user == null || user.getUserId() == null) {
            throw new ValidationException("Phiên đăng nhập không hợp lệ");
        }
        String current = form.getCurrentPassword();
        String next = form.getNewPassword();
        String confirm = form.getConfirmPassword();

        if (current == null || current.isBlank()) {
            throw new ValidationException("Vui lòng nhập mật khẩu hiện tại");
        }
        if (next == null || next.isBlank()) {
            throw new ValidationException("Vui lòng nhập mật khẩu mới");
        }
        if (next.length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException("Mật khẩu mới phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự");
        }
        if (confirm == null || !next.equals(confirm)) {
            throw new ValidationException("Xác nhận mật khẩu không khớp");
        }

        SysUser row = SqlExecutor.execute(SysUserMapper.class,
                m -> m.selectByPrimaryKey(user.getUserId().intValue()));
        if (row == null) {
            throw new ValidationException("Không tìm thấy tài khoản");
        }
        if (!securityService.matches(current, row.getPasswordHash())) {
            throw new ValidationException("Mật khẩu hiện tại không đúng");
        }

        String hash = securityService.hash(next);
        SqlExecutor.executeMapper(SysUserMapper.class, m -> {
            m.updatePasswordHash(user.getUserId().intValue(), hash);
            return null;
        });
    }

    private String resolveSingleRealm(List<String> roleNames) {
        Set<String> roles = new HashSet<>();
        if (roleNames != null) {
            for (String name : roleNames) {
                if (name != null && !name.isBlank()) {
                    roles.add(name.trim().toUpperCase());
                }
            }
        }
        if (roles.size() != 1) {
            throw new ValidationException("Tài khoản phải có đúng một vai trò ADMIN hoặc USER");
        }
        String realm = roles.iterator().next();
        if (!"ADMIN".equals(realm) && !"USER".equals(realm)) {
            throw new ValidationException("Vai trò tài khoản không được hỗ trợ");
        }
        return realm;
    }
}
