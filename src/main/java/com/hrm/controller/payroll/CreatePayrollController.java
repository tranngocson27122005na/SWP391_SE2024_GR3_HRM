package com.hrm.controller.payroll;

import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.BusinessException;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.service.payroll.PayrollRunService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Calendar;

@WebServlet("/payslip/create")
public class CreatePayrollController extends HttpServlet {

    private final PayrollRunService service = new PayrollRunService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Calendar now = Calendar.getInstance();
        if (request.getAttribute("periodMonth") == null) {
            request.setAttribute("periodMonth", now.get(Calendar.MONTH) + 1);
        }
        if (request.getAttribute("periodYear") == null) {
            request.setAttribute("periodYear", now.get(Calendar.YEAR));
        }
        request.getRequestDispatcher("/WEB-INF/views/payroll/payroll-create.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession user = SessionManager.getUserSession(request);
        String monthRaw = request.getParameter("periodMonth");
        String yearRaw = request.getParameter("periodYear");
        request.setAttribute("periodMonth", monthRaw);
        request.setAttribute("periodYear", yearRaw);
        try {
            int periodMonth = Integer.parseInt(monthRaw.trim());
            int periodYear = Integer.parseInt(yearRaw.trim());
            PayrollRunService.RunResult result = service.run(user, periodMonth, periodYear);
            response.sendRedirect(request.getContextPath()
                    + "/payslip/list?batchId=" + result.getBatchId()
                    + "&created=" + result.getCreatedCount());
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Kỳ lương không hợp lệ");
            doGet(request, response);
        } catch (UnauthorizedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch (BusinessException e) {
            request.setAttribute("errorMessage", e.getMessage());
            doGet(request, response);
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null && root.getCause() != root) {
                root = root.getCause();
            }
            String msg = root.getMessage() == null ? root.getClass().getSimpleName() : root.getMessage();
            request.setAttribute("errorMessage", "Lỗi tính lương: " + msg);
            e.printStackTrace();
            doGet(request, response);
        }
    }
}
