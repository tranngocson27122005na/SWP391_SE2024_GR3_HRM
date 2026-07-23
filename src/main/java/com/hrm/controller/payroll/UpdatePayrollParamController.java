package com.hrm.controller.payroll;

import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.BusinessException;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.service.payroll.PayrollParamService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/payslip/update")
public class UpdatePayrollParamController extends HttpServlet {

    private final PayrollParamService service = new PayrollParamService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession user = SessionManager.getUserSession(request);
        try {
            Map<String, BigDecimal> updates = new LinkedHashMap<>();
            Enumeration<String> names = request.getParameterNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                if (!name.startsWith("param_")) {
                    continue;
                }
                String code = name.substring("param_".length());
                String raw = request.getParameter(name);
                if (raw == null || raw.isBlank()) {
                    continue;
                }
                updates.put(code, new BigDecimal(raw.trim()));
            }
            service.updateValues(user, updates);
            response.sendRedirect(request.getContextPath() + "/payslip/edit?ok=1");
        } catch (UnauthorizedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Giá trị số không hợp lệ");
            request.setAttribute("params", service.listAll(user));
            request.getRequestDispatcher("/WEB-INF/views/payroll/payroll-param-edit.jsp")
                    .forward(request, response);
        } catch (BusinessException e) {
            request.setAttribute("errorMessage", e.getMessage());
            try {
                request.setAttribute("params", service.listAll(user));
            } catch (Exception ignored) {
                // keep going
            }
            request.getRequestDispatcher("/WEB-INF/views/payroll/payroll-param-edit.jsp")
                    .forward(request, response);
        }
    }
}
