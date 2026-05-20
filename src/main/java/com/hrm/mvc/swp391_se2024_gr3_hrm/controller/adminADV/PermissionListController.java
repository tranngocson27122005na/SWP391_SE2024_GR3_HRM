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
        String roleIdStr = req.getParameter("roleId");
        List<Permission> permissions;
        Integer selectedRoleId = null;

        if (roleIdStr != null && !roleIdStr.trim().isEmpty()) {
            try {
                selectedRoleId = Integer.parseInt(roleIdStr);
                permissions = permissionService.loadPermissionsByRole(selectedRoleId);
            } catch (NumberFormatException e) {
                permissions = permissionService.loadPermissions();
            }
        } else {
            permissions = permissionService.loadPermissions();
        }

        List<Role> roles = roleService.loadRoles();
        Map<Integer, String> roleMap = roles.stream()
                .collect(Collectors.toMap(Role::getRoleId, Role::getRoleName, (r1, r2) -> r1));

        req.setAttribute("permissions", permissions);
        req.setAttribute("roles", roles);
        req.setAttribute("roleMap", roleMap);
        req.setAttribute("selectedRoleId", selectedRoleId);
        req.getRequestDispatcher("/view/admin-advance/permission-list.jsp").forward(req, resp);
    }
}
