package com.hrm.mvc.swp391_se2024_gr3_hrm.controller.common;

import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account;
import com.hrm.mvc.swp391_se2024_gr3_hrm.service.AccountService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/change-password")
public class ChangePasswordController extends HttpServlet {

    private AccountService accountService;

    @Override
    public void init() throws ServletException {
        accountService = new AccountService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        Account account = (session != null) ? (Account) session.getAttribute("account") : null;

        // Nếu chưa đăng nhập, chuyển hướng sang trang đăng nhập
        if (account == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Hiển thị giao diện đổi mật khẩu
        req.getRequestDispatcher("/view/common/change-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Account account = (session != null) ? (Account) session.getAttribute("account") : null;

        // Nếu chưa đăng nhập, chuyển hướng sang trang đăng nhập
        if (account == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Lấy dữ liệu từ form
        String oldPassword = req.getParameter("oldPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        // Validate đầu vào
        if (oldPassword == null || oldPassword.trim().isEmpty() ||
            newPassword == null || newPassword.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty()) {
            
            req.setAttribute("error", "Vui lòng nhập đầy đủ các trường thông tin.");
            req.getRequestDispatcher("/view/common/change-password.jsp").forward(req, resp);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("error", "Xác nhận mật khẩu mới không khớp.");
            req.getRequestDispatcher("/view/common/change-password.jsp").forward(req, resp);
            return;
        }

        if (newPassword.equals(oldPassword)) {
            req.setAttribute("error", "Mật khẩu mới không được trùng với mật khẩu hiện tại.");
            req.getRequestDispatcher("/view/common/change-password.jsp").forward(req, resp);
            return;
        }

        // Gọi service xử lý cập nhật
        boolean isSuccess = accountService.changePassword(account.getAccountId(), oldPassword, newPassword);

        if (isSuccess) {
            // Cập nhật lại mật khẩu trong session
            account.setPassword(newPassword);
            session.setAttribute("account", account);
            
            req.setAttribute("success", "Mật khẩu đã được thay đổi thành công!");
        } else {
            req.setAttribute("error", "Mật khẩu hiện tại không chính xác.");
        }

        req.getRequestDispatcher("/view/common/change-password.jsp").forward(req, resp);
    }
}
