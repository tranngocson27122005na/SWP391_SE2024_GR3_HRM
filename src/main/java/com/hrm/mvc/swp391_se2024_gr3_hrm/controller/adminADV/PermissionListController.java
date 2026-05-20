package com.hrm.mvc.swp391_se2024_gr3_hrm.controller.adminADV;

import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Permission;
import com.hrm.mvc.swp391_se2024_gr3_hrm.service.PermissionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
@WebServlet("/permission-list")
public class PermissionListController extends HttpServlet {
    private PermissionService permissionService;

    @Override
    public void init() {
        permissionService = new PermissionService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Permission> permissions = permissionService.loadPermissions();
        req.setAttribute("permissions", permissions);
        req.getRequestDispatcher("/view/admin-advance/permission-list.jsp").forward(req, resp);
    }
}
