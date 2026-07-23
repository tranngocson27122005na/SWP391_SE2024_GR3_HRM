package com.hrm.controller.org.employee;

import com.hrm.dto.request.ContractFormRequest;
import com.hrm.dto.request.EmployeeFormRequest;
import com.hrm.dto.response.PermissionMatrixResponse.MatrixPositionRow;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.BusinessException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.persistence.executor.SqlExecutor;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.persistence.mapper.JobPositionMapper;
import com.hrm.service.org.EmployeeCommandService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/employee/create")
public class CreateEmployeeController extends HttpServlet {

    private final EmployeeCommandService commandService = new EmployeeCommandService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        loadPositions(request);
        request.getRequestDispatcher("/WEB-INF/views/org/employee/employee-create.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession user = SessionManager.getUserSession(request);
        EmployeeFormRequest emp = bindEmployee(request);
        ContractFormRequest con = bindContract(request);
        try {
            int id = commandService.createWithFirstContract(user, emp, con);
            response.sendRedirect(request.getContextPath() + "/employee/detail?id=" + id);
        } catch (BusinessException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("emp", emp);
            request.setAttribute("con", con);
            loadPositions(request);
            request.getRequestDispatcher("/WEB-INF/views/org/employee/employee-create.jsp")
                    .forward(request, response);
        }
    }

    private void loadPositions(HttpServletRequest request) {
        List<MatrixPositionRow> positions = SqlExecutor.execute(JobPositionMapper.class,
                JobPositionMapper::selectActiveWithDepartment);
        request.setAttribute("positions", positions);
    }

    private EmployeeFormRequest bindEmployee(HttpServletRequest request) {
        EmployeeFormRequest r = new EmployeeFormRequest();
        r.setEmployeeCode(request.getParameter("employeeCode"));
        r.setFullName(request.getParameter("fullName"));
        r.setGender(parseInt(request.getParameter("gender")));
        r.setBirthDate(request.getParameter("birthDate"));
        r.setBankAccount(request.getParameter("bankAccount"));
        r.setPositionId(parseLong(request.getParameter("positionId")));
        r.setEmploymentGroup(parseInt(request.getParameter("employmentGroup")));
        r.setJoiningDate(request.getParameter("joiningDate"));
        return r;
    }

    private ContractFormRequest bindContract(HttpServletRequest request) {
        ContractFormRequest r = new ContractFormRequest();
        r.setContractType(parseInt(request.getParameter("contractType")));
        r.setStartDate(request.getParameter("startDate"));
        r.setEndDate(request.getParameter("endDate"));
        r.setBasicSalary(request.getParameter("basicSalary"));
        r.setSalaryType(parseInt(request.getParameter("salaryType")));
        return r;
    }

    private Integer parseInt(String raw) {
        try {
            return raw == null || raw.isBlank() ? null : Integer.valueOf(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long parseLong(String raw) {
        try {
            return raw == null || raw.isBlank() ? null : Long.valueOf(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
