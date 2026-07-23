package com.hrm.persistence.mapper;

import com.hrm.persistence.entity.PayslipDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PayslipDetailMapper {
    int insert(PayslipDetail row);

    List<PayslipDetail> selectByPayslipId(@Param("payslipId") Integer payslipId);
}
