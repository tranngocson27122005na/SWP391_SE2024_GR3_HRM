package com.hrm.controller.org.contract;

import com.hrm.dto.response.ContractResponse;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.security.PositionPermissionMatrix;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.service.org.ContractService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@WebServlet("/contract/list")
public class ListContractController extends HttpServlet {

    private final ContractService service = new ContractService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession user = SessionManager.getUserSession(request);
        try {
            Integer employeeId = Integer.valueOf(request.getParameter("employeeId"));
            List<ContractResponse> contracts = service.listByEmployee(user, employeeId);
            Set<String> perms = PositionPermissionMatrix.permissionsOf(user.getPositionId());
            request.setAttribute("contracts", contracts);
            request.setAttribute("employeeId", employeeId);
            request.setAttribute("canCreate", perms.contains("contract:CREATE"));
            request.setAttribute("canUpdate", perms.contains("contract:UPDATE"));
            request.setAttribute("canDelete", perms.contains("contract:DELETE"));
            request.getRequestDispatcher("/WEB-INF/views/org/contract/contract-list.jsp")
                    .forward(request, response);
        } catch (NumberFormatException | ValidationException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (UnauthorizedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
