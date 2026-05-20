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
        HttpSession session = req.getSession(false);

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());

        if (session != null) {
            Account account = (Account) session.getAttribute("account"); // account đã lưu khi login
            if (account != null) {
                Integer roleId = account.getRoleId();

                if (roleId != null) {
                    // Chỉ chuyển hướng nếu truy cập trang chủ/login/root khi đã đăng nhập
                    if (path.equals("/") || path.equals("/login") || path.equals("/view/common/login.jsp") || path.equals("/view/common/home.jsp")) {
                        switch (roleId) {
                            case 1: // Common
                                req.getRequestDispatcher("/profile").forward(req, res);
                                return;
                            case 2: // Admin
                                req.getRequestDispatcher("/view/admin/user-list.jsp").forward(req, res);
                                return;
                            case 3: // Admin Advanced
                                req.getRequestDispatcher("/view/admin-advance/role-list.jsp").forward(req, res);
                                return;
                            default:
                                res.sendRedirect(req.getContextPath() + "/view/common/login.jsp");
                                return;
                        }
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }
}
