package com.hrm.controller.org.dependent;

import com.hrm.dto.response.DependentResponse;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.security.PositionPermissionMatrix;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.service.org.DependentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@WebServlet("/dependent/list")
public class ListDependentController extends HttpServlet {

    private final DependentService service = new DependentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession user = SessionManager.getUserSession(request);
        try {
            Integer employeeId = Integer.valueOf(request.getParameter("employeeId"));
            List<DependentResponse> dependents = service.list(user, employeeId);
            Set<String> perms = PositionPermissionMatrix.permissionsOf(user.getPositionId());
            request.setAttribute("dependents", dependents);
            request.setAttribute("employeeId", employeeId);
            request.setAttribute("canCreate", perms.contains("dependent:CREATE"));
            request.setAttribute("canDelete", perms.contains("dependent:DELETE"));
            request.getRequestDispatcher("/WEB-INF/views/org/dependent/dependent-list.jsp")
                    .forward(request, response);
        } catch (NumberFormatException | ValidationException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (UnauthorizedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
