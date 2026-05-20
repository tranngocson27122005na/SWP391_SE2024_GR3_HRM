package com.hrm.mvc.swp391_se2024_gr3_hrm.controller.common;

import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Profile;
import com.hrm.mvc.swp391_se2024_gr3_hrm.service.ProfileService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/profile")
public class ProfileController extends HttpServlet {

    private ProfileService profileService;

    @Override
    public void init() throws ServletException {
        profileService = new ProfileService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // Lấy session hiện tại
        HttpSession session = req.getSession(false);
        Account account = (session != null) ? (Account) session.getAttribute("account") : null;

        // Nếu chưa đăng nhập, chuyển hướng sang trang đăng nhập
        if (account == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Lấy citizenId từ tài khoản đăng nhập
        Integer citizenId = account.getCitizenId();
        Profile profile = null;

        if (citizenId != null) {
            profile = profileService.getProfileByCitizenId(citizenId);
        }

        // Gắn đối tượng profile vào request scope
        req.setAttribute("profile", profile);

        // Chuyển tiếp (forward) yêu cầu sang trang profile.jsp
        req.getRequestDispatcher("/view/common/profile.jsp").forward(req, resp);
    }
}
