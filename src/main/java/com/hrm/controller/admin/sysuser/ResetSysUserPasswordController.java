package com.hrm.controller.admin.sysuser;

import com.hrm.dto.request.ResetSysUserPasswordRequest;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.service.admin.SysUserAdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/sys-user/reset-password")
public class ResetSysUserPasswordController extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/admin/sys-user/sys-user-reset-password.jsp";

    private final SysUserAdminService service = new SysUserAdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("userId", request.getParameter("userId"));
        request.setAttribute("username", request.getParameter("username"));
        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ResetSysUserPasswordRequest form = new ResetSysUserPasswordRequest();
        try {
            form.setUserId(Long.valueOf(request.getParameter("userId")));
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Thiếu mã tài khoản");
            request.getRequestDispatcher(VIEW).forward(request, response);
            return;
        }
        form.setNewPassword(request.getParameter("newPassword"));
        form.setConfirmPassword(request.getParameter("confirmPassword"));
        request.setAttribute("userId", form.getUserId());
        request.setAttribute("username", request.getParameter("username"));

        UserSession admin = SessionManager.getUserSession(request);
        try {
            service.resetPassword(admin, form);
            response.sendRedirect(request.getContextPath() + "/sys-user/list");
        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher(VIEW).forward(request, response);
        }
    }
}
