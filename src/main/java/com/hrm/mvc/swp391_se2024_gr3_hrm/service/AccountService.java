package com.hrm.mvc.swp391_se2024_gr3_hrm.service;

import com.hrm.mvc.swp391_se2024_gr3_hrm.dto.form.LoginForm;
import com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.AccountMapper;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account;
import com.hrm.mvc.swp391_se2024_gr3_hrm.utility.executor.SqlExecutor;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountService {

    private static final Logger LOGGER = Logger.getLogger(AccountService.class.getName());

    // Gmail gửi mail — SMTP_USER phải trùng tài khoản đã tạo App Password
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USER = "tquan050624@gmail.com";
    private static final String SMTP_PASSWORD = "vgpa cwae zquj rofq";

    public Account login(String username, String password) {
        LoginForm form = new LoginForm();
        form.setUsername(username);
        form.setPassword(password);
        return SqlExecutor.execute(AccountMapper.class, false, mapper -> {
            Account account = mapper.selectByUsername(form.getUsername());
            if (account == null) return null;
            if (!account.getPassword().equals(form.getPassword())) return null;
            if (Boolean.FALSE.equals(account.getIsActive())) return null;
            return account;
        });
    }

    public boolean changePassword(Integer accountId, String oldPassword, String newPassword) {
        return SqlExecutor.execute(AccountMapper.class, true, mapper -> {
            Account account = mapper.selectByPrimaryKey(accountId);
            if (account == null) return false;
            if (!account.getPassword().equals(oldPassword)) return false;
            account.setPassword(newPassword);
            int rows = mapper.updateByPrimaryKey(account);
            return rows > 0;
        });
    }

    public Account findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return SqlExecutor.execute(AccountMapper.class, false,
                mapper -> mapper.selectByEmail(email.trim()));
    }

    public boolean updatePassword(Integer accountId, String newPassword) {
        if (accountId == null || newPassword == null || newPassword.isEmpty()) {
            LOGGER.warning("updatePassword: accountId hoặc mật khẩu mới không hợp lệ.");
            return false;
        }
        return SqlExecutor.execute(AccountMapper.class, true, mapper -> {
            Account account = mapper.selectByPrimaryKey(accountId);
            if (account == null) {
                LOGGER.log(Level.WARNING, "updatePassword: không tìm thấy accountId={0}", accountId);
                return false;
            }
            account.setPassword(newPassword);
            int rows = mapper.updateByPrimaryKey(account);
            if (rows <= 0) {
                LOGGER.log(Level.WARNING, "updatePassword: updateByPrimaryKey trả về {0} dòng", rows);
            }
            return rows > 0;
        });
    }

    public void sendEmail(String toEmail, String subject, String body) {
        if (!isSmtpConfigured()) {
            LOGGER.severe("SMTP chưa cấu hình. Sửa SMTP_USER và SMTP_PASSWORD trong AccountService.java.");
            throw new IllegalStateException("SMTP chưa được cấu hình.");
        }
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.trust", SMTP_HOST);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USER.trim(), normalizedSmtpPassword());
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USER.trim()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail.trim()));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);

            LOGGER.log(Level.INFO, "Email sent to {0}", toEmail.trim());
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send email to " + toEmail + ": " + e.getMessage(), e);
            throw new IllegalStateException("Không thể gửi email: " + e.getMessage(), e);
        }
    }

    private static String normalizedSmtpPassword() {
        if (SMTP_PASSWORD == null) {
            return "";
        }
        return SMTP_PASSWORD.replaceAll("\\s+", "");
    }

    private static boolean isSmtpConfigured() {
        if (SMTP_USER == null || SMTP_USER.trim().isEmpty()) {
            return false;
        }
        String password = normalizedSmtpPassword();
        return !password.isEmpty() && !password.contains("your-app-password");
    }
}
