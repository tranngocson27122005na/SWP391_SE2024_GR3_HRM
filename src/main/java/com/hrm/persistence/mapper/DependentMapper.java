package com.hrm.persistence.mapper;

import com.hrm.dto.response.DependentResponse;
import com.hrm.persistence.entity.Dependent;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DependentMapper {
    int insert(Dependent row);

    Dependent selectByPrimaryKey(Integer dependentId);

    List<DependentResponse> selectByEmployeeId(@Param("employeeId") Integer employeeId);

    int updateByPrimaryKey(Dependent row);

    int updateStatus(@Param("dependentId") Integer dependentId, @Param("status") Byte status);

    int countActiveByEmployeeId(@Param("employeeId") Integer employeeId);
}
