package com.hrm.controller.admin.sysuser;

import com.hrm.dto.response.EmployeeResponse;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.service.admin.SysUserAdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/sys-user/provision-list")
public class ListProvisionSysUserController extends HttpServlet {

    private final SysUserAdminService service = new SysUserAdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession admin = SessionManager.getUserSession(request);
        List<EmployeeResponse> employees = service.listEmployeesWithoutUser(admin);
        request.setAttribute("employees", employees);
        request.setAttribute("flashError", request.getSession().getAttribute("flashError"));
        request.getSession().removeAttribute("flashError");
        request.getRequestDispatcher("/WEB-INF/views/admin/sysuser/provision-list.jsp")
                .forward(request, response);
    }
}
