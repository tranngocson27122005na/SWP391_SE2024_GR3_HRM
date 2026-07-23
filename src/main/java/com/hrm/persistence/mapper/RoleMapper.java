package com.hrm.persistence.mapper;

import com.hrm.persistence.entity.Role;
import java.util.List;

public interface RoleMapper {
    int deleteByPrimaryKey(Integer roleId);

    int insert(Role row);

    Role selectByPrimaryKey(Integer roleId);

    List<Role> selectAll();

    Role selectByRoleName(String roleName);

    int updateByPrimaryKey(Role row);
}