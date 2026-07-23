package com.hrm.controller.payroll;

import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.persistence.entity.PayrollBatch;
import com.hrm.persistence.entity.Payslip;
import com.hrm.service.payroll.PayrollRunService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/payslip/list")
public class ListPayslipController extends HttpServlet {

    private final PayrollRunService service = new PayrollRunService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession user = SessionManager.getUserSession(request);
        try {
            List<PayrollBatch> batches = service.listBatches(user);
            request.setAttribute("batches", batches);

            Integer batchId = parseInt(request.getParameter("batchId"));
            if (batchId == null && !batches.isEmpty()) {
                batchId = batches.get(0).getBatchId();
            }
            if (batchId != null) {
                List<Payslip> payslips = service.listPayslips(user, batchId);
                request.setAttribute("batchId", batchId);
                request.setAttribute("payslips", payslips);
                for (PayrollBatch b : batches) {
                    if (batchId.equals(b.getBatchId())) {
                        request.setAttribute("currentBatch", b);
                        break;
                    }
                }
            }
            request.setAttribute("createdCount", request.getParameter("created"));
            request.getRequestDispatcher("/WEB-INF/views/payroll/payslip-list.jsp")
                    .forward(request, response);
        } catch (UnauthorizedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/payroll/payslip-list.jsp")
                    .forward(request, response);
        }
    }

    private static Integer parseInt(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
