package com.hrm.mvc.swp391_se2024_gr3_hrm.service;

import com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.ProfileMapper;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Profile;
import com.hrm.mvc.swp391_se2024_gr3_hrm.utility.executor.SqlExecutor;

public class ProfileService {

    public Profile getProfileByCitizenId(Integer citizenId) {
        if (citizenId == null) {
            return null;
        }
        return SqlExecutor.execute(ProfileMapper.class, mapper -> mapper.selectByPrimaryKey(citizenId));
    }

    public boolean updateProfile(Profile profile) {
        if (profile == null || profile.getCitizenId() == null) {
            return false;
        }
        return SqlExecutor.execute(ProfileMapper.class, mapper -> mapper.updateByPrimaryKey(profile) > 0);
    }

    public boolean insertProfile(Profile profile) {
        if (profile == null || profile.getCitizenId() == null) {
            return false;
        }
        return SqlExecutor.execute(ProfileMapper.class, mapper -> mapper.insert(profile) > 0);
    }

    public Profile getProfileByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return SqlExecutor.execute(ProfileMapper.class, mapper -> mapper.selectByEmail(email.trim()));
    }

    public boolean isEmailTaken(String email, Integer exceptCitizenId) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return SqlExecutor.execute(ProfileMapper.class,
                mapper -> mapper.countByEmailExceptCitizen(email.trim(), exceptCitizenId) > 0);
    }
}
