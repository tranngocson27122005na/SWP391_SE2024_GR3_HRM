package com.hrm.controller.org.contract;

import com.hrm.dto.request.ContractFormRequest;
import com.hrm.dto.response.EmployeeResponse;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.BusinessException;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.service.org.ContractService;
import com.hrm.service.org.EmployeeQueryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;

@WebServlet("/contract/create")
public class CreateContractController extends HttpServlet {

    private final ContractService service = new ContractService();
    private final EmployeeQueryService employeeQuery = new EmployeeQueryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession user = SessionManager.getUserSession(request);
        try {
            Long employeeId = Long.valueOf(request.getParameter("employeeId"));
            prepareForm(request, user, employeeId, null);
            request.getRequestDispatcher("/WEB-INF/views/org/contract/contract-create.jsp")
                    .forward(request, response);
        } catch (NumberFormatException | ValidationException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (UnauthorizedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession user = SessionManager.getUserSession(request);
        ContractFormRequest form = bind(request);
        try {
            service.createOrSwitch(user, form);
            response.sendRedirect(request.getContextPath()
                    + "/contract/list?employeeId=" + form.getEmployeeId());
        } catch (BusinessException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("con", form);
            if (form.getEmployeeId() != null) {
                try {
                    prepareForm(request, user, form.getEmployeeId(), form);
                } catch (BusinessException ignored) {
                    request.setAttribute("employeeId", form.getEmployeeId());
                    request.setAttribute("firstContract", true);
                }
            }
            request.getRequestDispatcher("/WEB-INF/views/org/contract/contract-create.jsp")
                    .forward(request, response);
        }
    }

    private void prepareForm(HttpServletRequest request, UserSession user, Long employeeId,
                             ContractFormRequest form) {
        EmployeeResponse emp = employeeQuery.getDetail(user, employeeId);
        request.setAttribute("employeeId", emp.getEmployeeId());
        request.setAttribute("employeeName", emp.getFullName());
        request.setAttribute("employeeCode", emp.getEmployeeCode());
        request.setAttribute("firstContract", emp.getCurrentContractId() == null);
        if (form != null) {
            request.setAttribute("con", form);
        } else if (emp.getCurrentContractId() == null && emp.getJoiningDate() != null) {
            ContractFormRequest hint = new ContractFormRequest();
            hint.setEmployeeId(employeeId);
            hint.setContractType(2);
            hint.setSalaryType(1);
            hint.setBasicSalary("7500000");
            hint.setStartDate(new SimpleDateFormat("yyyy-MM-dd").format(emp.getJoiningDate()));
            request.setAttribute("con", hint);
        }
    }

    private ContractFormRequest bind(HttpServletRequest request) {
        ContractFormRequest r = new ContractFormRequest();
        try {
            r.setEmployeeId(Long.valueOf(request.getParameter("employeeId")));
        } catch (Exception e) {
            r.setEmployeeId(null);
        }
        try {
            r.setContractType(Integer.valueOf(request.getParameter("contractType")));
        } catch (Exception e) {
            r.setContractType(null);
        }
        r.setStartDate(request.getParameter("startDate"));
        r.setEndDate(request.getParameter("endDate"));
        r.setBasicSalary(request.getParameter("basicSalary"));
        try {
            r.setSalaryType(Integer.valueOf(request.getParameter("salaryType")));
        } catch (Exception e) {
            r.setSalaryType(null);
        }
        return r;
    }
}
