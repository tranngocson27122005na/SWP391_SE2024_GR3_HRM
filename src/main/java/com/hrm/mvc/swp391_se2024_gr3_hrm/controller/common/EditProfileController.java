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
import java.text.ParseException;
import java.text.SimpleDateFormat;

@WebServlet("/edit-profile")
public class EditProfileController extends HttpServlet {

    private static final String DATE_PATTERN = "yyyy-MM-dd";

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
        req.getRequestDispatcher("/view/common/edit-profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        Account account = session != null ? (Account) session.getAttribute("account") : null;
        if (account == null || account.getCitizenId() == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Profile profile = profileService.getProfileByCitizenId(account.getCitizenId());
        if (profile == null) {
            req.setAttribute("error", "Không tìm thấy hồ sơ.");
            req.getRequestDispatcher("/view/common/edit-profile.jsp").forward(req, resp);
            return;
        }

        profile.setFullName(trim(req.getParameter("fullName")));
        profile.setEmail(trim(req.getParameter("email")));
        profile.setPhone(trim(req.getParameter("phone")));
        profile.setAddress(trim(req.getParameter("address")));

        String dob = trim(req.getParameter("dateOfBirth"));
        if (dob != null && !dob.isEmpty()) {
            try {
                profile.setDateOfBirth(new SimpleDateFormat(DATE_PATTERN).parse(dob));
            } catch (ParseException e) {
                req.setAttribute("error", "Ngày sinh không hợp lệ (yyyy-MM-dd).");
                req.setAttribute("profile", profile);
                req.getRequestDispatcher("/view/common/edit-profile.jsp").forward(req, resp);
                return;
            }
        }

        String gender = req.getParameter("gender");
        if (gender != null && !gender.isEmpty()) {
            profile.setGender("1".equals(gender));
        }

        String married = req.getParameter("married");
        if (married != null && !married.isEmpty()) {
            profile.setMarried("1".equals(married));
        }

        if (profile.getFullName() == null || profile.getFullName().isEmpty()) {
            req.setAttribute("error", "Họ tên là bắt buộc.");
            req.setAttribute("profile", profile);
            req.getRequestDispatcher("/view/common/edit-profile.jsp").forward(req, resp);
            return;
        }

        if (profileService.isEmailTaken(profile.getEmail(), profile.getCitizenId())) {
            req.setAttribute("error", "Email đã được sử dụng.");
            req.setAttribute("profile", profile);
            req.getRequestDispatcher("/view/common/edit-profile.jsp").forward(req, resp);
            return;
        }

        boolean updated = profileService.updateProfile(profile);
        if (!updated) {
            req.setAttribute("error", "Không thể cập nhật hồ sơ.");
            req.setAttribute("profile", profile);
            req.getRequestDispatcher("/view/common/edit-profile.jsp").forward(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/profile?success=updated");
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
