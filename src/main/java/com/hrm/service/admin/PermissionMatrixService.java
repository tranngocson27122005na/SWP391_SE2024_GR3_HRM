package com.hrm.service.admin;

import com.hrm.dto.request.PermissionMatrixUpdateRequest;
import com.hrm.dto.response.PermissionMatrixResponse;
import com.hrm.dto.response.PermissionMatrixResponse.MatrixPermissionCol;
import com.hrm.dto.response.PermissionMatrixResponse.MatrixPositionRow;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.persistence.executor.SqlExecutor;
import com.hrm.infrastructure.security.PositionPermissionMatrix;
import com.hrm.persistence.entity.Permission;
import com.hrm.persistence.entity.PositionPermission;
import com.hrm.persistence.entity.enums.PermissionAction;
import com.hrm.persistence.entity.enums.PositionDataScope;
import com.hrm.persistence.mapper.JobPositionMapper;
import com.hrm.persistence.mapper.PermissionMapper;
import com.hrm.persistence.mapper.PositionPermissionMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PermissionMatrixService {

    public PermissionMatrixResponse getMatrix(UserSession admin) {
        assertAdmin(admin);

        List<MatrixPositionRow> positions = SqlExecutor.execute(JobPositionMapper.class,
                JobPositionMapper::selectActiveWithDepartment);
        if (positions == null) {
            positions = new ArrayList<>();
        }
        for (MatrixPositionRow row : positions) {
            row.setDataScopeLabel(PositionDataScope.labelOf(row.getDataScope()));
        }

        List<Permission> catalog = SqlExecutor.execute(PermissionMapper.class, PermissionMapper::selectAll);
        List<MatrixPermissionCol> cols = new ArrayList<>();
        if (catalog != null) {
            for (Permission p : catalog) {
                MatrixPermissionCol col = new MatrixPermissionCol();
                col.setPermissionId(p.getPermissionId());
                col.setPermissionName(p.getPermissionName());
                col.setResource(p.getResource());
                col.setAction(p.getAction());
                PermissionAction action = PermissionAction.fromCode(p.getAction());
                col.setActionLabel(action == null ? String.valueOf(p.getAction()) : action.name());
                cols.add(col);
            }
        }

        List<PositionPermission> grants = SqlExecutor.execute(PositionPermissionMapper.class,
                PositionPermissionMapper::selectAll);
        Set<String> keys = new HashSet<>();
        if (grants != null) {
            for (PositionPermission g : grants) {
                if (g.getPositionId() != null && g.getPermissionId() != null) {
                    keys.add(g.getPositionId() + "_" + g.getPermissionId());
                }
            }
        }

        PermissionMatrixResponse response = new PermissionMatrixResponse();
        response.setPositions(positions);
        response.setPermissions(cols);
        response.setGrantedKeys(keys);
        return response;
    }

    public void updateMatrix(UserSession admin, PermissionMatrixUpdateRequest req) {
        assertAdmin(admin);
        if (req == null) {
            throw new ValidationException("Dữ liệu ma trận không hợp lệ");
        }

        List<MatrixPositionRow> activePositions = SqlExecutor.execute(JobPositionMapper.class,
                JobPositionMapper::selectActiveWithDepartment);
        Set<Integer> activePositionIds = new HashSet<>();
        if (activePositions != null) {
            for (MatrixPositionRow row : activePositions) {
                if (row.getPositionId() != null) {
                    activePositionIds.add(row.getPositionId());
                }
            }
        }

        List<Permission> catalog = SqlExecutor.execute(PermissionMapper.class, PermissionMapper::selectAll);
        Set<Integer> permissionIds = new HashSet<>();
        if (catalog != null) {
            for (Permission p : catalog) {
                if (p.getPermissionId() != null) {
                    permissionIds.add(p.getPermissionId());
                }
            }
        }

        Set<String> pairs = new HashSet<>();
        for (String token : req.getAssignments()) {
            if (token == null || token.isBlank()) {
                continue;
            }
            String[] parts = token.trim().split("_", 2);
            if (parts.length != 2) {
                throw new ValidationException("Assignment không hợp lệ: " + token);
            }
            int positionId;
            int permissionId;
            try {
                positionId = Integer.parseInt(parts[0]);
                permissionId = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                throw new ValidationException("Assignment không hợp lệ: " + token);
            }
            if (!activePositionIds.contains(positionId)) {
                throw new ValidationException("Chức vụ không hợp lệ hoặc không còn active");
            }
            if (!permissionIds.contains(permissionId)) {
                throw new ValidationException("Permission không tồn tại trong catalog");
            }
            pairs.add(positionId + "_" + permissionId);
        }

        Integer grantedBy = admin.getUserId() == null ? null : admin.getUserId().intValue();
        Date now = new Date();

        SqlExecutor.executeTransaction(session -> {
            PositionPermissionMapper mapper = session.getMapper(PositionPermissionMapper.class);
            for (Integer positionId : activePositionIds) {
                mapper.deleteByPositionId(positionId);
            }
            for (String pair : pairs) {
                String[] parts = pair.split("_", 2);
                PositionPermission row = new PositionPermission();
                row.setPositionId(Integer.parseInt(parts[0]));
                row.setPermissionId(Integer.parseInt(parts[1]));
                row.setGrantedBy(grantedBy);
                row.setGrantedAt(now);
                mapper.insert(row);
            }
            return null;
        });

        PositionPermissionMatrix.reload();
    }

    private void assertAdmin(UserSession admin) {
        if (admin == null || !admin.isAdmin()) {
            throw new UnauthorizedException("Chỉ ADMIN được thực hiện thao tác này");
        }
    }
}
