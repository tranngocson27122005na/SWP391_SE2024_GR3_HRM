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
import java.util.List;

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

    public List<Account> loadAllAccounts() {
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            try (SqlSession session = sqlSessionFactory.openSession()) {
                AccountMapper mapper = session.getMapper(AccountMapper.class);
                return mapper.selectAll();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean toggleAccountStatus(Integer accountId) {
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                AccountMapper mapper = session.getMapper(AccountMapper.class);
                Account account = mapper.selectByPrimaryKey(accountId);
                if (account != null) {
                    account.setIsActive(!account.getIsActive());
                    mapper.updateByPrimaryKey(account);
                    return true;
                }
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Account> getAllAccounts(int page, int pageSize) {
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            try (SqlSession session = sqlSessionFactory.openSession()) {
                AccountMapper mapper = session.getMapper(AccountMapper.class);
                List<Account> allAccounts = mapper.selectAll();

                int fromIndex = (page - 1) * pageSize;
                if (fromIndex >= allAccounts.size() || fromIndex < 0) {
                    return java.util.Collections.emptyList();
                }
                int toIndex = Math.min(fromIndex + pageSize, allAccounts.size());
                return allAccounts.subList(fromIndex, toIndex);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Account getAccountById(int id) {
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            try (SqlSession session = sqlSessionFactory.openSession()) {
                AccountMapper mapper = session.getMapper(AccountMapper.class);
                return mapper.selectByPrimaryKey(id);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean createAccount(Account account) {
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            try (SqlSession session = sqlSessionFactory.openSession(true)) { // openSession(true) để auto commit khi insert
                AccountMapper mapper = session.getMapper(AccountMapper.class);
                int rows = mapper.insert(account);
                return rows > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean changePassword(Integer accountId, String oldPassword, String newPassword) {
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                AccountMapper mapper = session.getMapper(AccountMapper.class);

                // Lấy account theo id
                Account account = mapper.selectByPrimaryKey(accountId);
                if (account == null) return false;

                // Kiểm tra mật khẩu cũ có khớp không
                if (!account.getPassword().equals(oldPassword)) return false;

                // Cập nhật mật khẩu mới
                account.setPassword(newPassword);
                int rows = mapper.updateByPrimaryKey(account);
                return rows > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public Account findByEmail(String email) {
        try {
            // Đọc config MyBatis
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

            // Mở session (auto commit = true)
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                AccountMapper mapper = session.getMapper(AccountMapper.class);

                // Gọi mapper để tìm account theo email
                Account account = mapper.selectByEmail(email);

                return account; // nếu không có thì trả về null
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}





