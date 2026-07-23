package com.hrm.persistence.mapper;

import com.hrm.persistence.entity.PayrollParam;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PayrollParamMapper {
    List<PayrollParam> selectAll();

    int updateValue(@Param("paramCode") String paramCode, @Param("paramValue") BigDecimal paramValue);
}
