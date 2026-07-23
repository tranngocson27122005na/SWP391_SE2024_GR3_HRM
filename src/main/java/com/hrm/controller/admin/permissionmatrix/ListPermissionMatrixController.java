package com.hrm.controller.admin.permissionmatrix;

import com.hrm.dto.response.PermissionMatrixResponse;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.service.admin.PermissionMatrixService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/permission-matrix/list")
public class ListPermissionMatrixController extends HttpServlet {

    private final PermissionMatrixService service = new PermissionMatrixService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession admin = SessionManager.getUserSession(request);
        PermissionMatrixResponse matrix = service.getMatrix(admin);
        request.setAttribute("matrix", matrix);
        Object flash = request.getSession().getAttribute("flashSuccess");
        if (flash != null) {
            request.setAttribute("successMessage", flash);
            request.getSession().removeAttribute("flashSuccess");
        }
        request.getRequestDispatcher("/WEB-INF/views/admin/permission-matrix/permission-matrix-list.jsp")
                .forward(request, response);
    }
}
