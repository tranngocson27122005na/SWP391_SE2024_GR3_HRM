package com.hrm.mvc.swp391_se2024_gr3_hrm.service;

import com.hrm.mvc.swp391_se2024_gr3_hrm.dto.form.LoginForm;
import com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.AccountMapper;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

public class AccountService {

    public Account login(LoginForm loginForm) {

        try {
            // Đọc file cấu hình MyBatis
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

            // Mở session
            try (SqlSession session = sqlSessionFactory.openSession()) {

                AccountMapper mapper = session.getMapper(AccountMapper.class);
                Account account = mapper.selectByUsername(loginForm.getUsername());

                if (account == null) return null;                          // username không tồn tại
                if (!account.getPassword().equals(loginForm.getPassword())) return null;  // sai mật khẩu
                if (Boolean.FALSE.equals(account.getIsActive())) return null;

                return account;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}





