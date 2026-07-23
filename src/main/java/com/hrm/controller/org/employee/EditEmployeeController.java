package com.hrm.controller.org.employee;

import com.hrm.dto.request.EmployeeFormRequest;
import com.hrm.dto.response.EmployeeResponse;
import com.hrm.dto.response.PermissionMatrixResponse.MatrixPositionRow;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.persistence.executor.SqlExecutor;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.persistence.mapper.JobPositionMapper;
import com.hrm.service.org.EmployeeCommandService;
import com.hrm.service.org.EmployeeQueryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/employee/edit")
public class EditEmployeeController extends HttpServlet {

    private final EmployeeQueryService queryService = new EmployeeQueryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession user = SessionManager.getUserSession(request);
        try {
            Long id = Long.valueOf(request.getParameter("id"));
            EmployeeResponse emp = queryService.getDetail(user, id);
            request.setAttribute("emp", emp);
            request.setAttribute("joiningDate", format(emp.getJoiningDate()));
            request.setAttribute("birthDate", format(emp.getBirthDate()));
            List<MatrixPositionRow> positions = SqlExecutor.execute(JobPositionMapper.class,
                    JobPositionMapper::selectActiveWithDepartment);
            request.setAttribute("positions", positions);
            request.getRequestDispatcher("/WEB-INF/views/org/employee/employee-edit.jsp")
                    .forward(request, response);
        } catch (NumberFormatException | ValidationException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (UnauthorizedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    private String format(java.util.Date d) {
        if (d == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(d);
    }
}
