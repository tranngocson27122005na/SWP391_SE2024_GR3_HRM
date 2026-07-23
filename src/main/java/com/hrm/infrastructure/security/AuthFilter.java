package com.hrm.infrastructure.security;

import com.hrm.dto.session.UserSession;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

/**
 * Authentication + realm authorization (common-auth design).
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/login",
            "/logout",
            "/error"
    );

    private static final Set<String> LOGIN_ONLY_PATHS = Set.of(
            "/home",
            "/change-password",
            "/employee/me"
    );

    private static final String[] PUBLIC_PREFIXES = {
            "/static/"
    };

    private static final String[] ADMIN_PREFIXES = {
            "/sys-user/",
            "/permission-matrix/"
    };

    private static final String[] USER_BUSINESS_PREFIXES = {
            "/employee/",
            "/contract/",
            "/dependent/",
            "/attendance/",
            "/payslip/"
    };

    @Override
    public void init(FilterConfig filterConfig) {
        PositionPermissionMatrix.load();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String contextPath = req.getContextPath();
        String path = req.getRequestURI().substring(contextPath.length());
        if (path.isEmpty()) {
            path = "/";
        }

        if (isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        UserSession user = SessionManager.getUserSession(req);
        if (user == null) {
            res.sendRedirect(contextPath + "/login");
            return;
        }

        if (LOGIN_ONLY_PATHS.contains(path)) {
            if ("/employee/me".equals(path)) {
                if (!user.isUser() || user.getEmployeeId() == null) {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            }
            chain.doFilter(request, response);
            return;
        }

        if (startsWithAny(path, ADMIN_PREFIXES) || isAdminExactList(path)) {
            if (!user.isAdmin()) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        if (startsWithAny(path, USER_BUSINESS_PREFIXES)) {
            if (!user.isUser()) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            // Self detail (tương thích): USER xem đúng emp phiên — dùng chung, không ma trận
            if (isOwnEmployeeDetail(path, req, user)) {
                chain.doFilter(request, response);
                return;
            }
            if (!PositionPermissionMatrix.hasPermission(user.getPositionId(), path)) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        res.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    private boolean isOwnEmployeeDetail(String path, HttpServletRequest req, UserSession user) {
        if (!"/employee/detail".equals(path) || user.getEmployeeId() == null) {
            return false;
        }
        String idRaw = req.getParameter("id");
        if (idRaw == null || idRaw.isBlank()) {
            return false;
        }
        try {
            return user.getEmployeeId().equals(Long.valueOf(idRaw.trim()));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isAdminExactList(String path) {
        return "/sys-user".equals(path) || "/permission-matrix".equals(path);
    }

    private boolean isPublic(String path) {
        if ("/".equals(path) || "/index.jsp".equals(path)) {
            return true;
        }
        if (PUBLIC_PATHS.contains(path)) {
            return true;
        }
        for (String prefix : PUBLIC_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private boolean startsWithAny(String path, String[] prefixes) {
        for (String prefix : prefixes) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
    }
}
