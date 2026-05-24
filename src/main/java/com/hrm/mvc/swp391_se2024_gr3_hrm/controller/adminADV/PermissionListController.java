package com.hrm.mvc.swp391_se2024_gr3_hrm.controller.adminADV;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

@WebServlet("/permission-list")
public class PermissionListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String roleId = req.getParameter("roleId");
        String redirect = req.getContextPath() + "/admin-advance/role-list";
        if (roleId != null && !roleId.isEmpty()) {
            redirect = req.getContextPath() + "/admin-advance/role-permissions?roleId=" + roleId;
        }
        resp.sendRedirect(redirect);
    }
}
