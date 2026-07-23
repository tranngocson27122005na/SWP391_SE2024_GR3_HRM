package com.hrm.controller.org.employee;

import com.hrm.dto.response.EmployeeResponse;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.security.PositionPermissionMatrix;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.service.org.DependentService;
import com.hrm.service.org.EmployeeQueryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

@WebServlet("/employee/me")
public class MyEmployeeController extends HttpServlet {

    private final EmployeeQueryService service = new EmployeeQueryService();
    private final DependentService dependentService = new DependentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession user = SessionManager.getUserSession(request);
        try {
            EmployeeResponse employee = service.getSelf(user);
            Set<String> perms = PositionPermissionMatrix.permissionsOf(
                    user == null ? null : user.getPositionId());
            request.setAttribute("employee", employee);
            request.setAttribute("profileSelf", true);
            request.setAttribute("canUpdate", false);
            request.setAttribute("canContractRead", perms.contains("contract:READ"));
            request.setAttribute("canDependentRead", true);
            request.setAttribute("dependentCount",
                    dependentService.countActive(employee.getEmployeeId().intValue()));
            request.getRequestDispatcher("/WEB-INF/views/org/employee/employee-detail.jsp")
                    .forward(request, response);
        } catch (ValidationException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (UnauthorizedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
