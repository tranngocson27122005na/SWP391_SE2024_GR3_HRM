package com.hrm.controller.admin.sysuser;

import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.BusinessException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.service.admin.SysUserAdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/sys-user/provision")
public class ProvisionSysUserController extends HttpServlet {

    private final SysUserAdminService service = new SysUserAdminService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession admin = SessionManager.getUserSession(request);
        try {
            Integer employeeId = Integer.valueOf(request.getParameter("employeeId"));
            service.provisionUserForEmployee(admin, employeeId);
        } catch (NumberFormatException | BusinessException e) {
            String msg = e instanceof NumberFormatException ? "Nhân viên không hợp lệ" : e.getMessage();
            request.getSession().setAttribute("flashError", msg);
        }
        response.sendRedirect(request.getContextPath() + "/sys-user/provision-list");
    }
}
