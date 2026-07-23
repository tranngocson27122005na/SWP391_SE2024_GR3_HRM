package com.hrm.controller.attendance;

import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.BusinessException;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.service.attendance.AttendanceImportService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

@WebServlet("/attendance/import")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024)
public class ImportAttendanceController extends HttpServlet {

    private final AttendanceImportService service = new AttendanceImportService();

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
        request.getRequestDispatcher("/WEB-INF/views/attendance/attendance-import.jsp")
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
            Part filePart = request.getPart("csvFile");
            if (filePart == null || filePart.getSize() <= 0) {
                throw new BusinessException("Chọn file CSV");
            }
            String fileName = filePart.getSubmittedFileName();
            try (InputStream in = filePart.getInputStream()) {
                AttendanceImportService.ImportResult result =
                        service.importCsv(user, in, fileName, periodMonth, periodYear);
                request.setAttribute("successMessage",
                        "Import thành công " + result.getSuccessCount() + " dòng (importId="
                                + result.getImportId() + ").");
                request.setAttribute("importErrors", result.getErrors());
            }
            doGet(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Kỳ lương không hợp lệ");
            doGet(request, response);
        } catch (UnauthorizedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch (BusinessException e) {
            request.setAttribute("errorMessage", e.getMessage());
            doGet(request, response);
        }
    }
}
