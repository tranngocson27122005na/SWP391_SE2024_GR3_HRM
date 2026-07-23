package com.hrm.persistence.mapper;

import com.hrm.persistence.entity.Payslip;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PayslipMapper {
    int insert(Payslip row);

    List<Payslip> selectByBatch(@Param("batchId") Integer batchId);

    Payslip selectByPrimaryKey(Integer payslipId);
}
