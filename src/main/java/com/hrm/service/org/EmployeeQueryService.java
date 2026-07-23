package com.hrm.service.org;

import com.hrm.dto.response.EmployeeResponse;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.persistence.executor.SqlExecutor;
import com.hrm.persistence.entity.enums.EmploymentGroup;
import com.hrm.persistence.entity.enums.Gender;
import com.hrm.persistence.entity.enums.PositionDataScope;
import com.hrm.persistence.mapper.EmployeeMapper;
import com.hrm.utility.Paging;

import java.util.Collections;
import java.util.List;

public class EmployeeQueryService {

    public List<EmployeeResponse> getList(UserSession user, Paging paging, String keyword) {
        assertUser(user);
        ScopeParams scope = resolveScope(user);
        if ("SELF".equals(scope.type)) {
            throw new UnauthorizedException("Không có quyền xem danh sách nhân viên");
        }
        String kw = keyword == null || keyword.isBlank() ? null : keyword.trim();

        int total = SqlExecutor.execute(EmployeeMapper.class,
                m -> m.countActiveByScope(kw, scope.type, scope.selfEmployeeId, scope.departmentId));
        paging.setSize(total);
        paging.calc();
        if (total == 0) {
            return Collections.emptyList();
        }

        List<EmployeeResponse> rows = SqlExecutor.execute(EmployeeMapper.class,
                m -> m.selectActiveByScope(kw, scope.type, scope.selfEmployeeId, scope.departmentId,
                        paging.getStart(), paging.getNrpp()));
        if (rows == null) {
            return Collections.emptyList();
        }
        for (EmployeeResponse row : rows) {
            enrichLabels(row);
        }
        return rows;
    }

    public EmployeeResponse getSelf(UserSession user) {
        assertUser(user);
        if (user.getEmployeeId() == null) {
            throw new ValidationException("Thiếu mã nhân viên");
        }
        EmployeeResponse self = SqlExecutor.execute(EmployeeMapper.class,
                m -> m.selectDetailByScope(user.getEmployeeId().intValue(), "SELF",
                        user.getEmployeeId(), null));
        if (self == null) {
            throw new UnauthorizedException("Không tìm thấy hồ sơ của bạn");
        }
        enrichLabels(self);
        return self;
    }

    public EmployeeResponse getDetail(UserSession user, Long employeeId) {
        assertUser(user);
        if (employeeId == null) {
            throw new ValidationException("Thiếu mã nhân viên");
        }
        // Hồ sơ cá nhân của chính mình: cho phép khi có employee-self (Filter đã check);
        // vẫn load qua scope SELF để không lộ NV khác.
        if (user.getEmployeeId() != null && user.getEmployeeId().equals(employeeId)) {
            EmployeeResponse self = SqlExecutor.execute(EmployeeMapper.class,
                    m -> m.selectDetailByScope(employeeId.intValue(), "SELF",
                            user.getEmployeeId(), null));
            if (self != null) {
                enrichLabels(self);
                return self;
            }
        }
        ScopeParams scope = resolveScope(user);
        EmployeeResponse row = SqlExecutor.execute(EmployeeMapper.class,
                m -> m.selectDetailByScope(employeeId.intValue(), scope.type,
                        scope.selfEmployeeId, scope.departmentId));
        if (row == null) {
            throw new UnauthorizedException("Không có quyền xem nhân viên này");
        }
        enrichLabels(row);
        return row;
    }

    private void enrichLabels(EmployeeResponse row) {
        row.setGenderLabel(Gender.labelOf(row.getGender()));
        row.setEmploymentGroupLabel(EmploymentGroup.labelOf(row.getEmploymentGroup()));
        row.setWorking(row.getCurrentContractId() != null);
    }

    private void assertUser(UserSession user) {
        if (user == null || !user.isUser()) {
            throw new UnauthorizedException("Chỉ USER được truy cập dữ liệu nhân viên");
        }
    }

    private ScopeParams resolveScope(UserSession user) {
        PositionDataScope scope = PositionDataScope.fromCode(user.getDataScope());
        if (scope == null) {
            return new ScopeParams("NONE", null, null);
        }
        return switch (scope) {
            case ALL -> new ScopeParams("ALL", null, null);
            case DEPARTMENT -> new ScopeParams("DEPARTMENT", null, user.getDepartmentId());
            case SELF -> new ScopeParams("SELF", user.getEmployeeId(), null);
        };
    }

    private static final class ScopeParams {
        private final String type;
        private final Long selfEmployeeId;
        private final Long departmentId;

        private ScopeParams(String type, Long selfEmployeeId, Long departmentId) {
            this.type = type;
            this.selfEmployeeId = selfEmployeeId;
            this.departmentId = departmentId;
        }
    }
}
