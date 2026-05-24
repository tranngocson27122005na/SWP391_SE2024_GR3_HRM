package com.hrm.mvc.swp391_se2024_gr3_hrm.service;

import com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.RoleMapper;
import com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.RolePermissionMapper;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Permission;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Role;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.RolePermission;
import com.hrm.mvc.swp391_se2024_gr3_hrm.utility.executor.SqlExecutor;
import com.hrm.mvc.swp391_se2024_gr3_hrm.utility.executor.SqlSessionTransaction;

import java.util.ArrayList;
import java.util.List;

public class RoleService {

    public List<Role> getAllRoles() {
        return SqlExecutor.execute(RoleMapper.class, RoleMapper::selectAll);
    }

    public List<Role> getActiveRoles() {
        return SqlExecutor.execute(RoleMapper.class, RoleMapper::selectActiveRoles);
    }

    public Role getRoleById(Integer roleId) {
        if (roleId == null) {
            return null;
        }
        return SqlExecutor.execute(RoleMapper.class, mapper -> mapper.selectByPrimaryKey(roleId));
    }

    public boolean updateRole(Role role) {
        if (role == null || role.getRoleId() == null) {
            return false;
        }
        return SqlExecutor.execute(RoleMapper.class, mapper -> mapper.updateByPrimaryKey(role) > 0);
    }

    public boolean toggleActive(Integer roleId, boolean active) {
        if (roleId == null) {
            return false;
        }
        return SqlExecutor.execute(RoleMapper.class, mapper -> {
            Role role = mapper.selectByPrimaryKey(roleId);
            if (role == null) {
                return false;
            }
            role.setIsActive(active);
            return mapper.updateByPrimaryKey(role) > 0;
        });
    }

    public List<Permission> getPermissionsByRoleId(Integer roleId) {
        if (roleId == null) {
            return new ArrayList<>();
        }
        return SqlExecutor.execute(RolePermissionMapper.class,
                mapper -> mapper.selectPermissionsByRoleId(roleId));
    }

    public List<Integer> getPermissionIdsByRoleId(Integer roleId) {
        if (roleId == null) {
            return new ArrayList<>();
        }
        return SqlExecutor.execute(RolePermissionMapper.class,
                mapper -> mapper.selectPermissionIdsByRoleId(roleId));
    }

    public boolean updateRolePermissions(Integer roleId, List<Integer> permissionIds) {
        if (roleId == null) {
            return false;
        }
        return SqlSessionTransaction.executeTransaction(session -> {
            RolePermissionMapper rpMapper = session.getMapper(RolePermissionMapper.class);
            rpMapper.deleteByRoleId(roleId);
            if (permissionIds != null && !permissionIds.isEmpty()) {
                List<RolePermission> rows = new ArrayList<>();
                for (Integer permissionId : permissionIds) {
                    if (permissionId != null) {
                        RolePermission row = new RolePermission();
                        row.setRoleId(roleId);
                        row.setPermissionId(permissionId);
                        rows.add(row);
                    }
                }
                if (!rows.isEmpty()) {
                    rpMapper.insertBatch(rows);
                }
            }
            return true;
        });
    }
}
