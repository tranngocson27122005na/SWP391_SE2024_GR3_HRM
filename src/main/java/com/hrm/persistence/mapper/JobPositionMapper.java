package com.hrm.persistence.mapper;

import com.hrm.dto.response.PermissionMatrixResponse.MatrixPositionRow;
import com.hrm.persistence.entity.JobPosition;
import java.util.List;

public interface JobPositionMapper {
    int deleteByPrimaryKey(Integer positionId);

    int insert(JobPosition row);

    JobPosition selectByPrimaryKey(Integer positionId);

    List<JobPosition> selectAll();

    int updateByPrimaryKey(JobPosition row);

    List<MatrixPositionRow> selectActiveWithDepartment();
}