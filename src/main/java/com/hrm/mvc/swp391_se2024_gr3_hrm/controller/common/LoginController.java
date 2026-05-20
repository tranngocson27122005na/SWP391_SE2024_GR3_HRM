package com.hrm.mvc.swp391_se2024_gr3_hrm.controller.common;

import com.hrm.mvc.swp391_se2024_gr3_hrm.dto.form.LoginForm;
import com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.AccountMapper;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import com.hrm.mvc.swp391_se2024_gr3_hrm.service.AccountService;

import java.io.IOException;
import java.io.Reader;

@WebServlet("/login")
public class LoginController extends HttpServlet {

    private AccountService accountService;
    @Override
    public void init(ServletConfig config) throws ServletException {
        accountService = new AccountService();
        Account account = new Account();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/view/common/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Lấy dữ liệu từ form và khởi tạo LoginForm
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        LoginForm loginForm = new LoginForm();
        loginForm.setUsername(username);
        loginForm.setPassword(password);

        Account account = accountService.login(loginForm);

        if (account != null) {
            HttpSession session = req.getSession();
            session.setAttribute("account", account);

            // Điều hướng theo role
            resp.sendRedirect(req.getContextPath() + "/view/common/home.jsp");
        } else {
            req.setAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
            req.getRequestDispatcher("/view/common/login.jsp").forward(req, resp);
        }
    }
}
