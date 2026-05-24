package com.hrm.mvc.swp391_se2024_gr3_hrm.controller.common;

import com.hrm.mvc.swp391_se2024_gr3_hrm.service.AccountService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

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
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email");
        if (email == null || email.trim().isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập email.");
            req.getRequestDispatcher("/view/common/forgot-password.jsp").forward(req, resp);
            return;
        }

        String newPassword = accountService.resetPassword(email.trim());
        if (newPassword == null) {
            req.setAttribute("error", "Không tìm thấy tài khoản với email này.");
            req.getRequestDispatcher("/view/common/forgot-password.jsp").forward(req, resp);
            return;
        }

        System.out.println("[FORGOT-PASSWORD] Email: " + email.trim() + " | New password: " + newPassword);
        req.setAttribute("message", "Mật khẩu mới đã được gửi tới email (xem log console để kiểm tra trong môi trường dev).");
        req.getRequestDispatcher("/view/common/forgot-password.jsp").forward(req, resp);
    }
}
