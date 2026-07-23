package com.hrm.controller.admin.permissionmatrix;

import com.hrm.dto.request.PermissionMatrixUpdateRequest;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.service.admin.PermissionMatrixService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;

@WebServlet("/permission-matrix/update")
public class UpdatePermissionMatrixController extends HttpServlet {

    private final PermissionMatrixService service = new PermissionMatrixService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession admin = SessionManager.getUserSession(request);
        PermissionMatrixUpdateRequest form = new PermissionMatrixUpdateRequest();
        String[] values = request.getParameterValues("assignment");
        if (values != null) {
            form.setAssignments(Arrays.asList(values));
        }

        try {
            service.updateMatrix(admin, form);
            request.getSession().setAttribute("flashSuccess", "Đã cập nhật ma trận phân quyền");
            response.sendRedirect(request.getContextPath() + "/permission-matrix/list");
        } catch (ValidationException e) {
            request.getSession().setAttribute("flashError", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/permission-matrix/list");
        }
    }
}
