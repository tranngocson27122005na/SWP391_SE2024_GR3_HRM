package com.hrm.mvc.swp391_se2024_gr3_hrm.service;


import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

public class FormmatService {
    public void formmatServce(){
        try {
            // Đọc file cấu hình MyBatis
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            // Mở session
            try (SqlSession session = sqlSessionFactory.openSession()) {

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
