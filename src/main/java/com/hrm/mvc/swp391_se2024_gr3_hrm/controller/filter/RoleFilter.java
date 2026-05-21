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

        // Lấy path hiện tại (bỏ context path ở đầu)
        String path = req.getRequestURI().substring(req.getContextPath().length());

        // 1. Cho phép các static resources đi qua (css, js, images, ...)
        if (path.startsWith("/static")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Cho phép login và logout đi qua mà không cần kiểm tra session
        if (path.equals("/login") || path.equals("/logout") || path.equals("/") || path.equals("/index.jsp")) {
            chain.doFilter(request, response);
            return;
        }

        // 3. Lấy thông tin session và account
        HttpSession session = req.getSession(false);
        Account account = (session != null) ? (Account) session.getAttribute("account") : null;

        // 4. Chưa đăng nhập → redirect về trang login
        if (account == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // 5. Đã đăng nhập → lấy roleId để kiểm tra quyền
        Integer roleId = account.getRoleId();

        // 6. Nếu truy cập root "/" hoặc "/login" khi đã đăng nhập → điều hướng theo role
        if (path.equals("/") || path.equals("/index.jsp")) {
            if (roleId != null) {
                switch (roleId) {
                    case 1: // Nhân viên thường
                        res.sendRedirect(req.getContextPath() + "/common/home");
                        return;
                    case 2: // Admin
                        res.sendRedirect(req.getContextPath() + "/admin/user-list");
                        return;
                    case 3: // Admin Advanced
                        res.sendRedirect(req.getContextPath() + "/admin-advance/role-list");
                        return;

                }
            }else{
                // Nếu chưa có roleId thì coi như khách → cho qua index.jsp
                chain.doFilter(request, response);
                return;}
        }

        // 7. Kiểm tra quyền truy cập vào các trang admin
        if (path.startsWith("/admin/") && roleId != null && roleId != 2 && roleId != 3) {
            res.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        if (path.startsWith("/admin-advance/") && roleId != null && roleId != 2 && roleId != 3) {
            res.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        if ((path.equals("/role-list") || path.equals("/permission-list")) && roleId != null && roleId != 3) {
            res.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        // 8. Mọi request hợp lệ còn lại → cho đi qua
        chain.doFilter(request, response);
    }
}
