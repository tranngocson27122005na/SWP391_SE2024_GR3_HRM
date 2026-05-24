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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet("/admin-advance/role-permissions")
public class RolePermissionController extends HttpServlet {

    private RoleService roleService;
    private PermissionService permissionService;

    @Override
    public void init() {
        roleService = new RoleService();
        permissionService = new PermissionService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer roleId = parseInteger(req.getParameter("roleId"));
        if (roleId == null) {
            resp.sendRedirect(req.getContextPath() + "/admin-advance/role-list?error=invalid");
            return;
        }

        Role role = roleService.getRoleById(roleId);
        if (role == null) {
            resp.sendRedirect(req.getContextPath() + "/admin-advance/role-list?error=notfound");
            return;
        }

        List<Permission> allPermissions = permissionService.getAllPermissions();
        List<Integer> assignedIds = roleService.getPermissionIdsByRoleId(roleId);
        Set<Integer> assignedSet = new HashSet<>(assignedIds);

        req.setAttribute("role", role);
        req.setAttribute("permissions", allPermissions);
        req.setAttribute("assignedPermissionIds", assignedSet);
        req.getRequestDispatcher("/view/admin-advance/role-permissions.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        Integer roleId = parseInteger(req.getParameter("roleId"));
        if (roleId == null) {
            resp.sendRedirect(req.getContextPath() + "/admin-advance/role-list?error=invalid");
            return;
        }

        String[] permissionIdParams = req.getParameterValues("permissionIds");
        List<Integer> permissionIds = new ArrayList<>();
        if (permissionIdParams != null) {
            for (String param : permissionIdParams) {
                Integer id = parseInteger(param);
                if (id != null) {
                    permissionIds.add(id);
                }
            }
        }

        roleService.updateRolePermissions(roleId, permissionIds);
        resp.sendRedirect(req.getContextPath() + "/admin-advance/role-permissions?roleId=" + roleId + "&success=updated");
    }

    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
