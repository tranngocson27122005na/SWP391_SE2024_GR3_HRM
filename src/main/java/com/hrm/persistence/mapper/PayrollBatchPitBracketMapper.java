package com.hrm.persistence.mapper;

import com.hrm.persistence.entity.PayrollBatchPitBracket;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PayrollBatchPitBracketMapper {
    int insert(PayrollBatchPitBracket row);

    int insertBatch(@Param("list") List<PayrollBatchPitBracket> list);
}
