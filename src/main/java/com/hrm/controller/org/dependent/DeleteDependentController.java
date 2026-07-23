package com.hrm.controller.org.dependent;

import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.service.org.DependentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/dependent/delete")
public class DeleteDependentController extends HttpServlet {

    private final DependentService service = new DependentService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession user = SessionManager.getUserSession(request);
        String employeeId = request.getParameter("employeeId");
        try {
            service.softDelete(user, Integer.valueOf(request.getParameter("id")));
        } catch (Exception ignored) {
        }
        response.sendRedirect(request.getContextPath() + "/dependent/list?employeeId=" + employeeId);
    }
}
