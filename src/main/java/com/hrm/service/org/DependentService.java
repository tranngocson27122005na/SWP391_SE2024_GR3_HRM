package com.hrm.service.org;

import com.hrm.dto.request.DependentFormRequest;
import com.hrm.dto.response.DependentResponse;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.persistence.executor.SqlExecutor;
import com.hrm.infrastructure.security.PositionPermissionMatrix;
import com.hrm.persistence.entity.Dependent;
import com.hrm.persistence.entity.enums.ActiveStatus;
import com.hrm.persistence.entity.enums.PositionDataScope;
import com.hrm.persistence.mapper.DependentMapper;
import com.hrm.utility.DateFormats;

import java.util.List;
import java.util.Set;

public class DependentService {

    private final EmployeeQueryService employeeQuery = new EmployeeQueryService();

    public List<DependentResponse> list(UserSession user, Integer employeeId) {
        assertCanAccess(user, employeeId, false);
        List<DependentResponse> rows = SqlExecutor.execute(DependentMapper.class,
                m -> m.selectByEmployeeId(employeeId));
        return rows == null ? List.of() : rows;
    }

    public int countActive(Integer employeeId) {
        Integer n = SqlExecutor.execute(DependentMapper.class, m -> m.countActiveByEmployeeId(employeeId));
        return n == null ? 0 : n;
    }

    public void create(UserSession user, DependentFormRequest req) {
        if (req == null || req.getEmployeeId() == null) {
            throw new ValidationException("Nhân viên không hợp lệ");
        }
        assertCanAccess(user, req.getEmployeeId().intValue(), true);
        validate(req);
        Dependent d = toEntity(req);
        d.setStatus(ActiveStatus.ACTIVE.toByte());
        SqlExecutor.execute(DependentMapper.class, m -> m.insert(d));
    }

    public void update(UserSession user, Integer dependentId, DependentFormRequest req) {
        Dependent existing = SqlExecutor.execute(DependentMapper.class, m -> m.selectByPrimaryKey(dependentId));
        if (existing == null) {
            throw new ValidationException("Người phụ thuộc không hợp lệ");
        }
        assertCanAccess(user, existing.getEmployeeId(), true);
        validate(req);
        existing.setFullName(req.getFullName().trim());
        existing.setRelationship(req.getRelationship().trim());
        existing.setTaxCode(blankToNull(req.getTaxCode()));
        existing.setStartDate(DateFormats.parseDate(req.getStartDate()));
        existing.setEndDate(DateFormats.parseDate(req.getEndDate()));
        SqlExecutor.execute(DependentMapper.class, m -> m.updateByPrimaryKey(existing));
    }

    public void softDelete(UserSession user, Integer dependentId) {
        Dependent existing = SqlExecutor.execute(DependentMapper.class, m -> m.selectByPrimaryKey(dependentId));
        if (existing == null) {
            throw new ValidationException("Người phụ thuộc không hợp lệ");
        }
        assertCanAccess(user, existing.getEmployeeId(), true);
        SqlExecutor.execute(DependentMapper.class,
                m -> m.updateStatus(dependentId, ActiveStatus.INACTIVE.toByte()));
    }

    private void assertCanAccess(UserSession user, Integer employeeId, boolean write) {
        if (user == null || !user.isUser()) {
            throw new UnauthorizedException("Chỉ USER được thao tác người phụ thuộc");
        }
        Set<String> perms = PositionPermissionMatrix.permissionsOf(user.getPositionId());
        boolean self = user.getEmployeeId() != null && user.getEmployeeId().equals(employeeId.longValue());
        if (self) {
            if (write && !perms.contains("dependent:CREATE") && !perms.contains("dependent:UPDATE")
                    && !perms.contains("dependent:DELETE")) {
                // self vẫn cần ít nhất một quyền dependent trong seed FAC-WRK
                if (!perms.contains("dependent:READ")) {
                    throw new UnauthorizedException("Không có quyền người phụ thuộc");
                }
            }
            return;
        }
        // HRS: phải xem được emp trong scope
        employeeQuery.getDetail(user, employeeId.longValue());
        PositionDataScope scope = PositionDataScope.fromCode(user.getDataScope());
        if (scope != PositionDataScope.ALL && scope != PositionDataScope.DEPARTMENT) {
            throw new UnauthorizedException("Không có quyền trên nhân viên này");
        }
    }

    private void validate(DependentFormRequest req) {
        if (req.getFullName() == null || req.getFullName().isBlank()) {
            throw new ValidationException("Họ tên người phụ thuộc không được để trống");
        }
        if (req.getRelationship() == null || req.getRelationship().isBlank()) {
            throw new ValidationException("Quan hệ không được để trống");
        }
    }

    private Dependent toEntity(DependentFormRequest req) {
        Dependent d = new Dependent();
        d.setEmployeeId(req.getEmployeeId().intValue());
        d.setFullName(req.getFullName().trim());
        d.setRelationship(req.getRelationship().trim());
        d.setTaxCode(blankToNull(req.getTaxCode()));
        d.setStartDate(DateFormats.parseDate(req.getStartDate()));
        d.setEndDate(DateFormats.parseDate(req.getEndDate()));
        return d;
    }

    private String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
