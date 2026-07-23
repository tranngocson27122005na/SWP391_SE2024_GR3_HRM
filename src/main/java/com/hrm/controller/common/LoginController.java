package com.hrm.controller.common;

import com.hrm.dto.request.LoginForm;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.service.common.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {

    private static final String LOGIN_VIEW = "/WEB-INF/views/common/login.jsp";

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionManager.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        request.getRequestDispatcher(LOGIN_VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LoginForm form = new LoginForm();
        form.setUsername(request.getParameter("username"));
        form.setPassword(request.getParameter("password"));
        request.setAttribute("form", form);

        String errorMessage = validateBasic(form);
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher(LOGIN_VIEW).forward(request, response);
            return;
        }

        try {
            // Mọi role đều vào /home sau đăng nhập (không phân nhánh theo role)
            UserSession userSession = authService.login(form);
            SessionManager.setUserSession(request, userSession);
            response.sendRedirect(request.getContextPath() + "/home");
        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher(LOGIN_VIEW).forward(request, response);
        }
    }

    private String validateBasic(LoginForm form) {
        if (form.getUsername() == null || form.getUsername().isBlank()) {
            return "Vui lòng nhập tên đăng nhập";
        }
        if (form.getPassword() == null || form.getPassword().isBlank()) {
            return "Vui lòng nhập mật khẩu";
        }
        return null;
    }
}
