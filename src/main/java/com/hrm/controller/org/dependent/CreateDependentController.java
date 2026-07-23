package com.hrm.controller.org.dependent;

import com.hrm.dto.request.DependentFormRequest;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.BusinessException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.service.org.DependentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/dependent/create")
public class CreateDependentController extends HttpServlet {

    private final DependentService service = new DependentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("employeeId", request.getParameter("employeeId"));
        request.getRequestDispatcher("/WEB-INF/views/org/dependent/dependent-create.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession user = SessionManager.getUserSession(request);
        DependentFormRequest form = bind(request);
        try {
            service.create(user, form);
            response.sendRedirect(request.getContextPath()
                    + "/dependent/list?employeeId=" + form.getEmployeeId());
        } catch (BusinessException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("form", form);
            request.setAttribute("employeeId", form.getEmployeeId());
            request.getRequestDispatcher("/WEB-INF/views/org/dependent/dependent-create.jsp")
                    .forward(request, response);
        }
    }

    private DependentFormRequest bind(HttpServletRequest request) {
        DependentFormRequest r = new DependentFormRequest();
        try {
            r.setEmployeeId(Long.valueOf(request.getParameter("employeeId")));
        } catch (Exception e) {
            r.setEmployeeId(null);
        }
        r.setFullName(request.getParameter("fullName"));
        r.setRelationship(request.getParameter("relationship"));
        r.setTaxCode(request.getParameter("taxCode"));
        r.setStartDate(request.getParameter("startDate"));
        r.setEndDate(request.getParameter("endDate"));
        return r;
    }
}
