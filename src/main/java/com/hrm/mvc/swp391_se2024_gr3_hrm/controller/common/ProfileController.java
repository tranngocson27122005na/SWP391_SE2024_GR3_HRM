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
    public void init() {
        profileService = new ProfileService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Account account = session != null ? (Account) session.getAttribute("account") : null;
        if (account == null || account.getCitizenId() == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Profile profile = profileService.getProfileByCitizenId(account.getCitizenId());
        req.setAttribute("profile", profile);
        req.setAttribute("account", account);

        String success = req.getParameter("success");
        if ("updated".equals(success)) {
            req.setAttribute("message", "Cập nhật hồ sơ thành công.");
        }

        req.getRequestDispatcher("/view/common/profile.jsp").forward(req, resp);
    }
}
