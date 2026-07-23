package com.hrm.controller.admin.sysuser;

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

@WebServlet("/sys-user/update-status")
public class UpdateSysUserStatusController extends HttpServlet {

    private final SysUserAdminService service = new SysUserAdminService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession admin = SessionManager.getUserSession(request);
        try {
            Long userId = Long.valueOf(request.getParameter("userId"));
            int status = Integer.parseInt(request.getParameter("status"));
            service.updateStatus(admin, userId, status);
            response.sendRedirect(request.getContextPath() + "/sys-user/list");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/sys-user/list");
        } catch (ValidationException e) {
            request.getSession().setAttribute("flashError", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/sys-user/list");
        }
    }
}
