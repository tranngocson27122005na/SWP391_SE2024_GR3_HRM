package com.hrm.persistence.mapper;

import com.hrm.dto.response.ContractResponse;
import com.hrm.persistence.entity.Contract;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ContractMapper {
    int insert(Contract row);

    Contract selectByPrimaryKey(Integer contractId);

    List<ContractResponse> selectByEmployeeId(@Param("employeeId") Integer employeeId);

    int updateByPrimaryKey(Contract row);

    int updateStatus(@Param("contractId") Integer contractId, @Param("status") Byte status);

    int inactiveAllActiveByEmployee(@Param("employeeId") Integer employeeId);
}
