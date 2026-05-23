package com.hrm.mvc.swp391_se2024_gr3_hrm.service;

import com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.AccountMapper;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

// phần riêng cho fotgot password
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
//

public class AccountService {

    public Account login(String username, String password) {
        try (Reader reader = Resources.getResourceAsReader("mybatis-config.xml")) {
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

            try (SqlSession session = sqlSessionFactory.openSession()) {
                AccountMapper mapper = session.getMapper(AccountMapper.class);
                Account account = mapper.selectByUsername(username);

                if (account == null) return null;                // username không tồn tại
                if (!account.getPassword().equals(password)) return null;  // sai mật khẩu
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

    // Hàm hash mật khẩu
    public String hashPassword(String plainPassword) {
        // 10 là "work factor" (độ phức tạp), bạn có thể tăng lên 12 hoặc 14
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }
    // Hàm kiểm tra mật khẩu khi login
    public boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
    public boolean updatePassword(Integer accountId, String newPassword) {
        try (Reader reader = Resources.getResourceAsReader("mybatis-config.xml")) {
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                AccountMapper mapper = session.getMapper(AccountMapper.class);

                Account account = mapper.selectByPrimaryKey(accountId);
                if (account == null) return false;
                // Hash mật khẩu trước khi lưu
                account.setPassword(hashPassword(newPassword));
                int rows = mapper.updateByPrimaryKey(account);
                return rows > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void sendEmail(String to, String subject, String body) {
        final String from = "yourgmail@gmail.com";
        final String password = "your-app-password"; // App Password từ Gmail

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}





