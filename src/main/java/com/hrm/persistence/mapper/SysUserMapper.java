package com.hrm.persistence.mapper;

import com.hrm.dto.response.SysUserResponse;
import com.hrm.persistence.entity.SysUser;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SysUserMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(SysUser row);

    SysUser selectByPrimaryKey(Integer userId);

    SysUser selectByUsername(String username);

    List<SysUser> selectAll();

    int updateByPrimaryKey(SysUser row);

    int updatePasswordHash(@Param("userId") Integer userId, @Param("passwordHash") String passwordHash);

    int updateStatus(@Param("userId") Integer userId, @Param("status") Byte status);

    int countAdminList(@Param("status") Integer status, @Param("keyword") String keyword);

    List<SysUserResponse> selectAdminList(@Param("status") Integer status,
                                         @Param("keyword") String keyword,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    int countActiveAdminsExcept(@Param("userId") Integer userId);
}