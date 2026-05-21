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

@WebServlet("/edit-permission")
public class EditPermissionController  extends HttpServlet {
    private PermissionService permissionService;
    private RoleService roleService;
    @Override
    public void init() {
        permissionService = new PermissionService();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer id = Integer.parseInt(req.getParameter("permissionId"));
        Permission permission = permissionService.findById(id);
        List<Role> roles = roleService.loadRoles();

        req.setAttribute("permission", permission);
        req.setAttribute("roles", roles);
        req.getRequestDispatcher("/view/admin-advance/edit-permission.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer id = Integer.parseInt(req.getParameter("permissionId"));
        String name = req.getParameter("permissionName");
        String description = req.getParameter("description");
        Integer roleId = Integer.parseInt(req.getParameter("roleId"));

        Permission permission = new Permission();
        permission.setPermissionId(id);
        permission.setPermissionName(name);
        permission.setDescription(description);
        permission.setRoleId(roleId);

        permissionService.updatePermission(permission);

        resp.sendRedirect(req.getContextPath() + "/permission-list");
    }
}
