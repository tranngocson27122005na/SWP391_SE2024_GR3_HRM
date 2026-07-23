package com.hrm.persistence.mapper;

import com.hrm.persistence.entity.AttendanceImport;
import java.util.List;

public interface AttendanceImportMapper {
    int deleteByPrimaryKey(Integer importId);

    int insert(AttendanceImport row);

    AttendanceImport selectByPrimaryKey(Integer importId);

    List<AttendanceImport> selectAll();

    int updateByPrimaryKey(AttendanceImport row);
}