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

@WebServlet("/contract/delete")
public class DeleteContractController extends HttpServlet {

    private final ContractService service = new ContractService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession user = SessionManager.getUserSession(request);
        String employeeId = request.getParameter("employeeId");
        try {
            Integer id = Integer.valueOf(request.getParameter("id"));
            service.softDelete(user, id);
        } catch (NumberFormatException | BusinessException e) {
            // redirect anyway
        }
        response.sendRedirect(request.getContextPath() + "/contract/list?employeeId=" + employeeId);
    }
}
