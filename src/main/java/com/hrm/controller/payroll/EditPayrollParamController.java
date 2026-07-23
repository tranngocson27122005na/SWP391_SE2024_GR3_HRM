package com.hrm.controller.payroll;

import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.persistence.entity.PayrollParam;
import com.hrm.service.payroll.PayrollParamService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/payslip/edit")
public class EditPayrollParamController extends HttpServlet {

    private final PayrollParamService service = new PayrollParamService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession user = SessionManager.getUserSession(request);
        try {
            List<PayrollParam> params = service.listAll(user);
            request.setAttribute("params", params);
            request.getRequestDispatcher("/WEB-INF/views/payroll/payroll-param-edit.jsp")
                    .forward(request, response);
        } catch (UnauthorizedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
