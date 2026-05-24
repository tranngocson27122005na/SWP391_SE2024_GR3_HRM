package com.hrm.mvc.swp391_se2024_gr3_hrm.mapper;

import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Permission;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.RolePermission;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RolePermissionMapper {
    int deleteByPrimaryKey(@Param("roleId") Integer roleId, @Param("permissionId") Integer permissionId);

    int insert(RolePermission row);

    List<RolePermission> selectAll();

    List<Integer> selectPermissionIdsByRoleId(@Param("roleId") Integer roleId);

    List<Permission> selectPermissionsByRoleId(@Param("roleId") Integer roleId);

    int deleteByRoleId(@Param("roleId") Integer roleId);

    int insertBatch(@Param("list") List<RolePermission> list);
}