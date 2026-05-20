package com.hrm.mvc.swp391_se2024_gr3_hrm.mapper;

import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Permission;
import java.util.List;

public interface PermissionMapper {
    int deleteByPrimaryKey(Integer permissionId);

    int insert(Permission row);

    Permission selectByPrimaryKey(Integer permissionId);

    List<Permission> selectAll();

    int updateByPrimaryKey(Permission row);
}