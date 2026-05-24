package com.hrm.mvc.swp391_se2024_gr3_hrm.controller.adminADV;

import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Role;
import com.hrm.mvc.swp391_se2024_gr3_hrm.service.RoleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin-advance/role-edit")
public class RoleEditController extends HttpServlet {

    private RoleService roleService;

    @Override
    public void init() {
        roleService = new RoleService();
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

        req.setAttribute("role", role);
        req.getRequestDispatcher("/view/admin-advance/role-edit.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Integer roleId = parseInteger(req.getParameter("roleId"));
        String roleName = trim(req.getParameter("roleName"));
        String roleDescription = trim(req.getParameter("roleDescription"));

        if (roleId == null || roleName == null || roleName.isEmpty()) {
            req.setAttribute("error", "Mã vai trò và tên vai trò là bắt buộc.");
            Role role = new Role();
            role.setRoleId(roleId);
            role.setRoleName(roleName);
            role.setRoleDescription(roleDescription);
            req.setAttribute("role", role);
            req.getRequestDispatcher("/view/admin-advance/role-edit.jsp").forward(req, resp);
            return;
        }

        Role existing = roleService.getRoleById(roleId);
        if (existing == null) {
            resp.sendRedirect(req.getContextPath() + "/admin-advance/role-list?error=notfound");
            return;
        }

        existing.setRoleName(roleName);
        existing.setRoleDescription(roleDescription);
        roleService.updateRole(existing);
        resp.sendRedirect(req.getContextPath() + "/admin-advance/role-list?success=updated");
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

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
