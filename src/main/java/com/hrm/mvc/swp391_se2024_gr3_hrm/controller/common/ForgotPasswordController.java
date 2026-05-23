package com.hrm.mvc.swp391_se2024_gr3_hrm.controller.common;

import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account;
import com.hrm.mvc.swp391_se2024_gr3_hrm.service.AccountService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.SecureRandom;

@WebServlet("/forgot-password")
public class ForgotPasswordController extends HttpServlet {

    private AccountService accountService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        accountService = new AccountService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/view/common/forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        Account account = accountService.findByEmail(email);

        if (account == null) {
            req.setAttribute("error", "Email không tồn tại trong hệ thống.");
            req.getRequestDispatcher("/view/common/forgot-password.jsp").forward(req, resp);
            return;
        }

        if (Boolean.FALSE.equals(account.getIsActive())) {
            req.setAttribute("error", "Tài khoản liên kết với email này đã bị khóa.");
            req.getRequestDispatcher("/view/common/forgot-password.jsp").forward(req, resp);
            return;
        }

        String newPassword = generateRandomPassword(8);
        String loginUrl = buildLoginUrl(req);

        try {
            accountService.sendEmail(email.trim(), "HRM System - Mật khẩu mới",
                    "Xin chào " + account.getUsername() + ",\n\n"
                            + "Mật khẩu mới: " + newPassword + "\n"
                            + "Đăng nhập tại: " + loginUrl + "\n"
                            + "Vui lòng đổi lại mật khẩu sau khi đăng nhập.");
        } catch (IllegalStateException e) {
            req.setAttribute("error", "Không thể gửi email. Kiểm tra cấu hình SMTP trong AccountService.java.");
            req.getRequestDispatcher("/view/common/forgot-password.jsp").forward(req, resp);
            return;
        }

        boolean updated = accountService.updatePassword(account.getAccountId(), newPassword);

        if (updated) {
            req.setAttribute("message", "Mật khẩu mới đã được gửi tới email của bạn.");
        } else {
            req.setAttribute("error", "Có lỗi xảy ra khi cập nhật mật khẩu.");
        }

        req.getRequestDispatcher("/view/common/forgot-password.jsp").forward(req, resp);
    }

    private String buildLoginUrl(HttpServletRequest req) {
        StringBuilder url = new StringBuilder();
        url.append(req.getScheme()).append("://").append(req.getServerName());
        int port = req.getServerPort();
        if (("http".equals(req.getScheme()) && port != 80)
                || ("https".equals(req.getScheme()) && port != 443)) {
            url.append(":").append(port);
        }
        url.append(req.getContextPath()).append("/login");
        return url.toString();
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
