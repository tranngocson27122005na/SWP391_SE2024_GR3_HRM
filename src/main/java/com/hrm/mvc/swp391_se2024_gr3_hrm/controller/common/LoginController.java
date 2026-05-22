package com.hrm.mvc.swp391_se2024_gr3_hrm.controller.common;

import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.hrm.mvc.swp391_se2024_gr3_hrm.service.AccountService;
import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {

    private AccountService accountService;
    @Override
    public void init(ServletConfig config) {
        accountService = new AccountService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/view/common/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        Account account = accountService.login(username,password);

        if (account != null) {
            HttpSession session = req.getSession();
            session.setAttribute("account", account);

            Integer roleId = account.getRoleId();
            if (roleId == 1) {
                resp.sendRedirect(req.getContextPath() + "/common/home");
            } else if (roleId == 2) {
                resp.sendRedirect(req.getContextPath() + "/admin/user-list");
            } else if (roleId == 3) {
                resp.sendRedirect(req.getContextPath() + "/admin-advance/role-list");
            } else {
                resp.sendRedirect(req.getContextPath() + "/");
            }
        } else {
            req.setAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
            req.getRequestDispatcher("/view/common/login.jsp").forward(req, resp);
        }
    }
}
