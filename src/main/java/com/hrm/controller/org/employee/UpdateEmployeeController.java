package com.hrm.controller.org.employee;

import com.hrm.dto.request.EmployeeFormRequest;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.BusinessException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.service.org.EmployeeCommandService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/employee/update")
public class UpdateEmployeeController extends HttpServlet {

    private final EmployeeCommandService commandService = new EmployeeCommandService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession user = SessionManager.getUserSession(request);
        try {
            Integer id = Integer.valueOf(request.getParameter("id"));
            EmployeeFormRequest emp = new EmployeeFormRequest();
            emp.setFullName(request.getParameter("fullName"));
            emp.setGender(parseInt(request.getParameter("gender")));
            emp.setBirthDate(request.getParameter("birthDate"));
            emp.setBankAccount(request.getParameter("bankAccount"));
            emp.setPositionId(parseLong(request.getParameter("positionId")));
            emp.setEmploymentGroup(parseInt(request.getParameter("employmentGroup")));
            emp.setJoiningDate(request.getParameter("joiningDate"));
            commandService.updateProfile(user, id, emp);
            response.sendRedirect(request.getContextPath() + "/employee/detail?id=" + id);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (BusinessException e) {
            request.getSession().setAttribute("flashError", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/employee/edit?id=" + request.getParameter("id"));
        }
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
