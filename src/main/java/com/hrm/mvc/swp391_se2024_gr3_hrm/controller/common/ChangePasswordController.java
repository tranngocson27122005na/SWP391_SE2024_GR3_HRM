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
    public void init(){
        accountService = new AccountService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if ("1".equals(req.getParameter("success"))) {
            req.setAttribute("message", "Đổi mật khẩu thành công.");
        }
        req.getRequestDispatcher("/view/common/change-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        Account account = session != null ? (Account) session.getAttribute("account") : null;
        if (account == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String oldPassword = req.getParameter("oldPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (oldPassword == null || oldPassword.isEmpty()
                || newPassword == null || newPassword.isEmpty()
                || confirmPassword == null || confirmPassword.isEmpty()) {
            req.setAttribute("error", "Vui lòng điền đầy đủ các trường.");
            req.getRequestDispatcher("/view/common/change-password.jsp").forward(req, resp);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("error", "Mật khẩu mới và xác nhận không khớp.");
            req.getRequestDispatcher("/view/common/change-password.jsp").forward(req, resp);
            return;
        }

        boolean changed = accountService.changePassword(account.getAccountId(), oldPassword, newPassword);
        if (!changed) {
            req.setAttribute("error", "Mật khẩu hiện tại không đúng.");
            req.getRequestDispatcher("/view/common/change-password.jsp").forward(req, resp);
            return;
        }

        account.setPassword(newPassword);
        session.setAttribute("account", account);
        resp.sendRedirect(req.getContextPath() + "/change-password?success=1");
    }
}
