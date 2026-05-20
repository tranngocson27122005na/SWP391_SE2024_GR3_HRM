package filter;

import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class RoleFilter implements Filter {

    // ── Định nghĩa role ──────────────────────────────────────────────
    private static final int ROLE_COMMON         = 1;
    private static final int ROLE_ADMIN          = 2;
    private static final int ROLE_ADMIN_ADVANCED = 3;

    // ── URL công khai (không cần login) ─────────────────────────────
    private static final String[] PUBLIC_URLS = {
            "/login",
            "/forgot-password"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getServletPath();

        // 1. Cho qua các URL công khai
        if (isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Chưa login → về trang login
        HttpSession session = req.getSession(false);
        Account account = (session != null) ? (Account) session.getAttribute("account") : null;

        if (account == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int roleId = account.getRoleId();

        // 3. Phân quyền theo path prefix
        if (path.startsWith("/admin-advanced") && roleId != ROLE_ADMIN_ADVANCED) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Chỉ Admin Advanced mới được truy cập.");
            return;
        }

        if (path.startsWith("/admin") && roleId != ROLE_ADMIN && roleId != ROLE_ADMIN_ADVANCED) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Chỉ Admin mới được truy cập.");
            return;
        }

        if (path.startsWith("/common") && roleId != ROLE_COMMON) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Không có quyền truy cập.");
            return;
        }

        // 4. Pass
        chain.doFilter(request, response);
    }

    private boolean isPublic(String path) {
        for (String url : PUBLIC_URLS) {
            if (path.equals(url) || path.startsWith(url + "/")) return true;
        }
        return false;
    }
}

