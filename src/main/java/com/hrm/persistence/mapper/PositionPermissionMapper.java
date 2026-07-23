package com.hrm.persistence.mapper;

import com.hrm.dto.response.PositionPermissionGrant;
import com.hrm.persistence.entity.PositionPermission;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PositionPermissionMapper {
    int deleteByPrimaryKey(@Param("positionId") Integer positionId, @Param("permissionId") Integer permissionId);

    int insert(PositionPermission row);

    PositionPermission selectByPrimaryKey(@Param("positionId") Integer positionId, @Param("permissionId") Integer permissionId);

    List<PositionPermission> selectAll();

    int updateByPrimaryKey(PositionPermission row);

    List<PositionPermissionGrant> selectAllGrants();

    List<String> selectPermissionNamesByPositionId(Integer positionId);

    int deleteByPositionId(Integer positionId);
}