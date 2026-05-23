package com.hrm.mvc.swp391_se2024_gr3_hrm.controller.adminADV;
import com.hrm.mvc.swp391_se2024_gr3_hrm.service.PermissionService;
import com.hrm.mvc.swp391_se2024_gr3_hrm.service.RoleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


@WebServlet("/edit-permission")
public class EditPermissionController  extends HttpServlet {
    private PermissionService permissionService;
    private RoleService roleService;
    @Override
    public void init() {
        permissionService = new PermissionService();
        roleService = new RoleService();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/view/admin-advance/edit-permission.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/permission-list");
    }
}
