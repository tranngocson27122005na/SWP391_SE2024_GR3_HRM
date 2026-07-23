package com.hrm.persistence.mapper;

import com.hrm.persistence.entity.PayrollBatch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PayrollBatchMapper {
    int insert(PayrollBatch row);

    int deleteAll();

    PayrollBatch selectLatestDraft();

    PayrollBatch selectByPrimaryKey(Integer batchId);

    List<PayrollBatch> selectAll();

    int updateTotals(@Param("batchId") Integer batchId, @Param("totalNet") java.math.BigDecimal totalNet);
}
