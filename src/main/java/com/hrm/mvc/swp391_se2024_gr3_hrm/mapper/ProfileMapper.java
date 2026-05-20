package com.hrm.mvc.swp391_se2024_gr3_hrm.mapper;

import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Profile;
import java.util.List;

public interface ProfileMapper {
    int deleteByPrimaryKey(Integer citizenId);

    int insert(Profile row);

    Profile selectByPrimaryKey(Integer citizenId);

    List<Profile> selectAll();

    int updateByPrimaryKey(Profile row);
}