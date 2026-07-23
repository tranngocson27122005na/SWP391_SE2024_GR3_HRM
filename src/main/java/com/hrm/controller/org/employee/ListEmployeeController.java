package com.hrm.controller.org.employee;

import com.hrm.dto.response.EmployeeResponse;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.security.PositionPermissionMatrix;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.service.org.EmployeeQueryService;
import com.hrm.utility.Paging;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@WebServlet("/employee/list")
public class ListEmployeeController extends HttpServlet {

    private final EmployeeQueryService service = new EmployeeQueryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession user = SessionManager.getUserSession(request);
        String keyword = request.getParameter("keyword");
        Paging paging = Paging.fromRequest(request);

        try {
            List<EmployeeResponse> employees = service.getList(user, paging, keyword);
            Set<String> perms = PositionPermissionMatrix.permissionsOf(user.getPositionId());
            request.setAttribute("employees", employees);
            request.setAttribute("paging", paging);
            request.setAttribute("pagingAction", request.getContextPath() + "/employee/list");
            request.setAttribute("keyword", keyword == null ? "" : keyword);
            request.setAttribute("canCreate", perms.contains("employee:CREATE"));
            request.getRequestDispatcher("/WEB-INF/views/org/employee/employee-list.jsp")
                    .forward(request, response);
        } catch (UnauthorizedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
