package com.hrm.controller.common;

import com.hrm.dto.request.ChangePasswordForm;
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

@WebServlet("/change-password")
public class ChangePasswordController extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/common/change-password.jsp";

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ChangePasswordForm form = new ChangePasswordForm();
        form.setCurrentPassword(request.getParameter("currentPassword"));
        form.setNewPassword(request.getParameter("newPassword"));
        form.setConfirmPassword(request.getParameter("confirmPassword"));
        request.setAttribute("form", form);

        UserSession user = SessionManager.getUserSession(request);
        try {
            authService.changePassword(user, form);
            response.sendRedirect(request.getContextPath() + "/home");
        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher(VIEW).forward(request, response);
        }
    }
}
