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

    // --- CHỨC NĂNG 7: XEM DANH SÁCH (Tận dụng selectAll() kết hợp RowBounds) ---
    public java.util.List<Account> getAllAccounts(int page, int pageSize) {
        try (SqlSession session = getSqlSessionFactory().openSession()) {
            int offset = (page - 1) * pageSize;
            org.apache.ibatis.session.RowBounds rowBounds = new org.apache.ibatis.session.RowBounds(offset, pageSize);

            // Gọi lệnh selectAll kết hợp rowBounds để MyBatis tự ngắt trang dữ liệu
            return session.selectList("com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.AccountMapper.selectAll", null, rowBounds);
        }
    }

    // --- CHỨC NĂNG 8: XEM CHI TIẾT (Tận dụng selectByPrimaryKey) ---
    public Account getAccountById(int accountId) {
        try (SqlSession session = getSqlSessionFactory().openSession()) {
            AccountMapper mapper = session.getMapper(AccountMapper.class);
            return mapper.selectByPrimaryKey(accountId);
        }
    }

    // --- CHỨC NĂNG 9: THÊM MỚI (Tận dụng insert) ---
    public boolean createAccount(Account newAccount) {
        try (SqlSession session = getSqlSessionFactory().openSession(true)) { // true để tự động commit xuống MySQL
            AccountMapper mapper = session.getMapper(AccountMapper.class);

            newAccount.setIsActive(true); // Mặc định tài khoản mới tạo sẽ được kích hoạt
            int rows = mapper.insert(newAccount);

            return rows > 0;
        }
    }
    // Thêm đoạn này vào bên trong class AccountService
    private SqlSessionFactory getSqlSessionFactory() {
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            return new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi cấu hình MyBatis: " + e.getMessage(), e);

        }
    }

}



