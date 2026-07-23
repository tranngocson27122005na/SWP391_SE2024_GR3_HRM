package com.hrm.persistence.mapper;

import com.hrm.dto.response.EmployeeResponse;
import com.hrm.persistence.entity.Employee;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EmployeeMapper {
    int insert(Employee row);

    Employee selectByPrimaryKey(Integer employeeId);

    Employee selectByCode(@Param("employeeCode") String employeeCode);

    List<Employee> selectAll();

    int updateByPrimaryKey(Employee row);

    int updateCurrentContractId(@Param("employeeId") Integer employeeId,
                                @Param("currentContractId") Integer currentContractId);

    List<EmployeeResponse> selectWithoutSysUser();

    int countActiveByScope(@Param("keyword") String keyword,
                           @Param("scopeType") String scopeType,
                           @Param("selfEmployeeId") Long selfEmployeeId,
                           @Param("departmentId") Long departmentId);

    List<EmployeeResponse> selectActiveByScope(@Param("keyword") String keyword,
                                               @Param("scopeType") String scopeType,
                                               @Param("selfEmployeeId") Long selfEmployeeId,
                                               @Param("departmentId") Long departmentId,
                                               @Param("offset") int offset,
                                               @Param("limit") int limit);

    EmployeeResponse selectDetailByScope(@Param("employeeId") Integer employeeId,
                                         @Param("scopeType") String scopeType,
                                         @Param("selfEmployeeId") Long selfEmployeeId,
                                         @Param("departmentId") Long departmentId);
}
