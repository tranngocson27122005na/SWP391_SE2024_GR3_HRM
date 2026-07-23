package com.hrm.persistence.mapper;

import com.hrm.persistence.entity.PayrollBatchParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PayrollBatchParamMapper {
    int insert(PayrollBatchParam row);

    int insertBatch(@Param("list") List<PayrollBatchParam> list);
}
