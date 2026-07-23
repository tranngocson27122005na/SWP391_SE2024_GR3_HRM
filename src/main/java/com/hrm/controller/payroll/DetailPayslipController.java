package com.hrm.controller.payroll;

import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.persistence.entity.Payslip;
import com.hrm.persistence.entity.PayslipDetail;
import com.hrm.service.payroll.PayrollRunService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/payslip/detail")
public class DetailPayslipController extends HttpServlet {

    private final PayrollRunService service = new PayrollRunService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession user = SessionManager.getUserSession(request);
        try {
            Integer payslipId = Integer.valueOf(request.getParameter("id").trim());
            Payslip payslip = service.getPayslip(user, payslipId);
            List<PayslipDetail> details = service.getDetails(user, payslipId);
            request.setAttribute("payslip", payslip);
            request.setAttribute("details", details);
            request.getRequestDispatcher("/WEB-INF/views/payroll/payslip-detail.jsp")
                    .forward(request, response);
        } catch (NumberFormatException | NullPointerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (UnauthorizedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch (ValidationException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
