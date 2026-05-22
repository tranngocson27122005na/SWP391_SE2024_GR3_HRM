package com.hrm.mvc.swp391_se2024_gr3_hrm.utility.executor;


import com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.AccountMapper;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

public class MapperTest {
    public static void main(String[] args) {
        try {
            // Đọc file cấu hình MyBatis
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

            // Mở session
            try (SqlSession session = sqlSessionFactory.openSession()) {


                // Lấy mapper runtime
                AccountMapper mapper = session.getMapper(AccountMapper.class);

                // Đổi "admin" thành username có trong DB của bạn
                String testUsername = "admin1";
                Account account = mapper.selectByUsername(testUsername);

                if (account == null) {
                    System.out.println("Không tìm thấy username: " + testUsername);
                } else {
                    System.out.println("accountId : " + account.getAccountId());
                    System.out.println("username  : " + account.getUsername());
                    System.out.println("password  : " + account.getPassword());
                    System.out.println("isActive  : " + account.getIsActive());
                    System.out.println("roleId    : " + account.getRoleId());
                    System.out.println("citizenId : " + account.getCitizenId());
                }
            }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
