package com.hrm.controller.org.contract;

import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.BusinessException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.service.org.ContractService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Activate HĐ — dùng POST /contract/update?action=activate */
@WebServlet("/contract/update")
public class UpdateContractController extends HttpServlet {

    private final ContractService service = new ContractService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession user = SessionManager.getUserSession(request);
        String employeeId = request.getParameter("employeeId");
        String action = request.getParameter("action");
        try {
            Integer id = Integer.valueOf(request.getParameter("id"));
            if ("activate".equalsIgnoreCase(action)) {
                service.activate(user, id);
            }
        } catch (NumberFormatException | BusinessException e) {
            // ignore → redirect
        }
        response.sendRedirect(request.getContextPath() + "/contract/list?employeeId=" + employeeId);
    }
}
