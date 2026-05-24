package com.hrm.mvc.swp391_se2024_gr3_hrm.service;

import com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.PermissionMapper;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Permission;
import com.hrm.mvc.swp391_se2024_gr3_hrm.utility.executor.SqlExecutor;

import java.util.List;

public class PermissionService {

    public List<Permission> getAllPermissions() {
        return SqlExecutor.execute(PermissionMapper.class, PermissionMapper::selectAll);
    }
}
