package com.hrm.mvc.swp391_se2024_gr3_hrm.controller.adminADV;

import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Permission;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Role;
import com.hrm.mvc.swp391_se2024_gr3_hrm.service.PermissionService;
import com.hrm.mvc.swp391_se2024_gr3_hrm.service.RoleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/permission-list")
public class PermissionListController extends HttpServlet {
    private PermissionService permissionService;
    private RoleService roleService;

    @Override
    public void init() {
        permissionService = new PermissionService();
        roleService = new RoleService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/view/admin-advance/permission-list.jsp").forward(req, resp);
    }
}
