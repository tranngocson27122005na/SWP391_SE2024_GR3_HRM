package com.hrm.persistence.mapper;

import com.hrm.persistence.entity.DailyAttendance;
import java.util.List;

public interface DailyAttendanceMapper {
    int deleteByPrimaryKey(Integer attendanceId);

    int insert(DailyAttendance row);

    DailyAttendance selectByPrimaryKey(Integer attendanceId);

    List<DailyAttendance> selectAll();

    int updateByPrimaryKey(DailyAttendance row);
}