package com.hrm.controller.admin.sysuser;

import com.hrm.dto.response.SysUserResponse;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.security.SessionManager;
import com.hrm.service.admin.SysUserAdminService;
import com.hrm.utility.Paging;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/sys-user/list")
public class ListSysUserController extends HttpServlet {

    private final SysUserAdminService service = new SysUserAdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession admin = SessionManager.getUserSession(request);
        Integer status = parseNullableInt(request.getParameter("status"));
        String keyword = request.getParameter("keyword");
        Paging paging = Paging.fromRequest(request);

        List<SysUserResponse> users = service.getList(admin, paging, status, keyword);
        request.setAttribute("users", users);
        request.setAttribute("paging", paging);
        request.setAttribute("pagingAction", request.getContextPath() + "/sys-user/list");
        request.setAttribute("keyword", keyword == null ? "" : keyword);
        request.setAttribute("statusFilter", status);
        request.getRequestDispatcher("/WEB-INF/views/admin/sys-user/sys-user-list.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private Integer parseNullableInt(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
