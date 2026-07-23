package com.hrm.persistence.mapper;

import com.hrm.persistence.entity.UserRole;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserRoleMapper {
    int deleteByPrimaryKey(@Param("userId") Integer userId, @Param("roleId") Integer roleId);

    int insert(UserRole row);

    List<UserRole> selectAll();

    List<String> selectRoleNamesByUserId(Integer userId);
}