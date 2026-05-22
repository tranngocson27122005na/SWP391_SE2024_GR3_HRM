package com.hrm.mvc.swp391_se2024_gr3_hrm.controller.adminADV;

import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Role;
import com.hrm.mvc.swp391_se2024_gr3_hrm.service.RoleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
@WebServlet("/admin-advance/role-list")
public class RoleListController extends HttpServlet {
    private RoleService roleService;

    @Override
    public void init() {
        roleService = new RoleService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Role> roles = roleService.loadRoles();
        req.setAttribute("roles", roles);
        req.getRequestDispatcher("/view/admin-advance/role-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String roleIdStr = request.getParameter("roleId");
        String isActiveStr = request.getParameter("isActive");

        if (roleIdStr != null && isActiveStr != null) {
            Integer roleId = Integer.parseInt(roleIdStr);
            boolean isActive = "1".equals(isActiveStr);

            boolean updated = roleService.updateRoleStatus(roleId, isActive);

            if (updated) {
                request.getSession().setAttribute("message", "update successfully");
            } else {
                request.getSession().setAttribute("message", "no find selected role");
            }
        } else {
            request.getSession().setAttribute("message", "information isn't valid");
        }

        response.sendRedirect(request.getContextPath() + "/role-list");
    }
}
