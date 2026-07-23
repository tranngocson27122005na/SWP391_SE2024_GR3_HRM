package com.hrm.persistence.mapper;

import com.hrm.persistence.entity.Department;
import java.util.List;

public interface DepartmentMapper {
    int deleteByPrimaryKey(Integer departmentId);

    int insert(Department row);

    Department selectByPrimaryKey(Integer departmentId);

    List<Department> selectAll();

    int updateByPrimaryKey(Department row);
}