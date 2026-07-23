package com.hrm.persistence.mapper;

import com.hrm.persistence.entity.AttendanceSummary;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AttendanceSummaryMapper {
    int deleteByPrimaryKey(Integer summaryId);

    int insert(AttendanceSummary row);

    AttendanceSummary selectByPrimaryKey(Integer summaryId);

    AttendanceSummary selectByEmpPeriod(@Param("employeeId") Integer employeeId,
                                        @Param("periodYear") Integer periodYear,
                                        @Param("periodMonth") Integer periodMonth);

    List<AttendanceSummary> selectByPeriod(@Param("periodYear") Integer periodYear,
                                           @Param("periodMonth") Integer periodMonth);

    List<AttendanceSummary> selectAll();

    int updateByPrimaryKey(AttendanceSummary row);
}
