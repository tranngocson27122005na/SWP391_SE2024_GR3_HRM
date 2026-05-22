package com.hrm.mvc.swp391_se2024_gr3_hrm.controller.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account;

@WebFilter("/*")  // Áp dụng cho tất cả request
public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getServletPath();

        if (path.equals("/login") || path.equals("/logout") || path.equals("/")|| path.equals("/index.jsp")){
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        Account account = (session != null) ? (Account) session.getAttribute("account") : null;

        if (account == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        Integer roleId = account.getRoleId();

        if (path.isEmpty()) {
            if (roleId != null) {
                switch (roleId) {
                    case 1:
                        res.sendRedirect(req.getContextPath() + "/common/home");
                        return;
                    case 2:
                        res.sendRedirect(req.getContextPath() + "/admin/user-list");
                        return;
                    case 3:
                        res.sendRedirect(req.getContextPath() + "/admin-advance/role-list");
                        return;

                }
            }else{
                chain.doFilter(request, response);
                return;}
        }

        if (path.startsWith("/admin/") && roleId != null && roleId != 2 && roleId != 3) {
            res.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        if (path.startsWith("/admin-advance/") && roleId != null && roleId != 2 && roleId != 3) {
            res.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }
        chain.doFilter(request, response);
    }
}
