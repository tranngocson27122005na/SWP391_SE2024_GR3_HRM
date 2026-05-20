package com.hrm.mvc.swp391_se2024_gr3_hrm.service;

import com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.ProfileMapper;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Profile;
import com.hrm.mvc.swp391_se2024_gr3_hrm.utility.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

public class ProfileService {

    public Profile getProfileByCitizenId(Integer citizenId) {
        if (citizenId == null) {
            return null;
        }
        
        // Sử dụng SqlSessionFactory đơn bản (singleton) từ MyBatisUtil để tránh quá tải kết nối
        try (SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ProfileMapper mapper = session.getMapper(ProfileMapper.class);
            return mapper.selectByPrimaryKey(citizenId);
        }
    }
}
