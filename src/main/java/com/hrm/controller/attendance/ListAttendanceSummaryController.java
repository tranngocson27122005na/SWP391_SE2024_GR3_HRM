package com.hrm.controller.attendance;

import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.persistence.entity.AttendanceSummary;
import com.hrm.service.attendance.AttendanceImportService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

@WebServlet("/attendance/list")
public class ListAttendanceSummaryController extends HttpServlet {

    private final AttendanceImportService service = new AttendanceImportService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession user = SessionManager.getUserSession(request);
        Calendar now = Calendar.getInstance();
        int periodMonth = parseOr(request.getParameter("periodMonth"), now.get(Calendar.MONTH) + 1);
        int periodYear = parseOr(request.getParameter("periodYear"), now.get(Calendar.YEAR));
        try {
            List<AttendanceSummary> rows = service.listByPeriod(user, periodMonth, periodYear);
            request.setAttribute("rows", rows);
            request.setAttribute("periodMonth", periodMonth);
            request.setAttribute("periodYear", periodYear);
            request.getRequestDispatcher("/WEB-INF/views/attendance/attendance-list.jsp")
                    .forward(request, response);
        } catch (UnauthorizedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("periodMonth", periodMonth);
            request.setAttribute("periodYear", periodYear);
            request.getRequestDispatcher("/WEB-INF/views/attendance/attendance-list.jsp")
                    .forward(request, response);
        }
    }

    private static int parseOr(String raw, int fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
