package com.hrm.mvc.swp391_se2024_gr3_hrm.controller.adminADV;

import com.hrm.mvc.swp391_se2024_gr3_hrm.service.RoleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin-advance/role-list")
public class RoleListController extends HttpServlet {
    private RoleService roleService;

    @Override
    public void init() {
        roleService = new RoleService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("roles", roleService.getAllRoles());

        String success = req.getParameter("success");
        if ("updated".equals(success)) {
            req.setAttribute("message", "Cập nhật vai trò thành công.");
        } else if ("toggle".equals(success)) {
            req.setAttribute("message", "Cập nhật trạng thái vai trò thành công.");
        }

        req.getRequestDispatcher("/view/admin-advance/role-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        Integer roleId = parseInteger(request.getParameter("roleId"));
        String isActiveParam = request.getParameter("isActive");

        if (roleId == null || isActiveParam == null) {
            response.sendRedirect(request.getContextPath() + "/admin-advance/role-list?error=invalid");
            return;
        }

        boolean active = "true".equalsIgnoreCase(isActiveParam);
        roleService.toggleActive(roleId, active);
        response.sendRedirect(request.getContextPath() + "/admin-advance/role-list?success=toggle");
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
