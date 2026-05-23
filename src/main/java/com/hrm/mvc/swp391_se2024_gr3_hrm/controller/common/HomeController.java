package com.hrm.mvc.swp391_se2024_gr3_hrm.controller.common;

import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/common/home")
public class HomeController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Lấy session hiện tại (không tạo mới)
        HttpSession session = req.getSession(false);

        if (session == null) {
            // Chưa có session -> chuyển về login
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Lấy account từ session (nếu bạn lưu tên khác, sửa cho khớp)
        Account account = (Account) session.getAttribute("account");

        if (account == null) {
            // Chưa đăng nhập -> chuyển về login
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Đặt attribute "user" để JSP sử dụng sessionScope.user.username
        // Nếu bạn đã lưu "user" trực tiếp trong session thì bước này có thể bỏ
        session.setAttribute("user", account);

        // Forward tới JSP view
        req.getRequestDispatcher("/view/common/home.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Nếu có form POST tới trang này, xử lý giống GET
        doGet(req, resp);
    }
}
