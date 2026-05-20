package com.hrm.mvc.swp391_se2024_gr3_hrm.service;

import com.hrm.mvc.swp391_se2024_gr3_hrm.dto.form.LoginForm;
import com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.AccountMapper;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.hrm.mvc.swp391_se2024_gr3_hrm.utility.MyBatisUtil;
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

    public boolean changePassword(Integer accountId, String oldPassword, String newPassword) {
        if (accountId == null || oldPassword == null || newPassword == null || oldPassword.trim().isEmpty() || newPassword.trim().isEmpty()) {
            return false;
        }

        try (SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession()) {
            AccountMapper mapper = session.getMapper(AccountMapper.class);
            Account account = mapper.selectByPrimaryKey(accountId);

            if (account == null) {
                return false;
            }

            // Kiểm tra mật khẩu cũ
            if (!account.getPassword().equals(oldPassword)) {
                return false;
            }

            // Cập nhật mật khẩu mới
            account.setPassword(newPassword);
            int result = mapper.updateByPrimaryKey(account);

            if (result > 0) {
                session.commit();
                return true;
            }
            return false;
        }
    }

}
