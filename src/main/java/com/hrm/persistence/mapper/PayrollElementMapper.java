package com.hrm.persistence.mapper;

import com.hrm.persistence.entity.PayrollElement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PayrollElementMapper {
    List<PayrollElement> selectAll();

    PayrollElement selectByCode(@Param("elementCode") String elementCode);
}
