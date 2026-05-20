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
import java.util.Date;

@WebServlet("/edit-profile")
public class EditProfileController extends HttpServlet {

    private ProfileService profileService;

    @Override
    public void init() throws ServletException {
        profileService = new ProfileService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Account account = (session != null) ? (Account) session.getAttribute("account") : null;

        if (account == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Integer citizenId = account.getCitizenId();
        if (citizenId == null) {
            req.setAttribute("error", "Tài khoản của bạn chưa được liên kết với bất kỳ hồ sơ công dân nào.");
            req.getRequestDispatcher("/view/common/profile.jsp").forward(req, resp);
            return;
        }

        Profile profile = profileService.getProfileByCitizenId(citizenId);
        if (profile == null) {
            req.setAttribute("error", "Hồ sơ công dân không tồn tại trong hệ thống.");
            req.getRequestDispatcher("/view/common/profile.jsp").forward(req, resp);
            return;
        }

        req.setAttribute("profile", profile);
        req.getRequestDispatcher("/view/common/edit-profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Account account = (session != null) ? (Account) session.getAttribute("account") : null;

        if (account == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Integer citizenId = account.getCitizenId();
        if (citizenId == null) {
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        Profile currentProfile = profileService.getProfileByCitizenId(citizenId);
        if (currentProfile == null) {
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        // Retrieve form parameters
        String fullName = req.getParameter("fullName");
        String dateOfBirthStr = req.getParameter("dateOfBirth");
        String gender = req.getParameter("gender");
        String nationality = req.getParameter("nationality");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String residence = req.getParameter("residence");

        // Simple validation
        if (fullName == null || fullName.trim().isEmpty() ||
            phone == null || phone.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {

            req.setAttribute("error", "Vui lòng nhập đầy đủ các trường thông tin bắt buộc (Họ tên, Điện thoại, Email).");
            // Set input attributes back so user doesn't lose their data
            Profile tempProfile = new Profile();
            tempProfile.setCitizenId(citizenId);
            tempProfile.setFullName(fullName);
            tempProfile.setGender(gender);
            tempProfile.setNationality(nationality);
            tempProfile.setPhone(phone);
            tempProfile.setEmail(email);
            tempProfile.setResidence(residence);
            if (dateOfBirthStr != null && !dateOfBirthStr.trim().isEmpty()) {
                try {
                    tempProfile.setDateOfBirth(new SimpleDateFormat("yyyy-MM-dd").parse(dateOfBirthStr));
                } catch (ParseException ignored) {}
            }
            req.setAttribute("profile", tempProfile);
            req.getRequestDispatcher("/view/common/edit-profile.jsp").forward(req, resp);
            return;
        }

        // Safe Date Parsing
        Date dateOfBirth = null;
        if (dateOfBirthStr != null && !dateOfBirthStr.trim().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                dateOfBirth = sdf.parse(dateOfBirthStr);
            } catch (ParseException e) {
                req.setAttribute("error", "Định dạng ngày sinh không hợp lệ.");
                // Forward back as well
                Profile tempProfile = new Profile();
                tempProfile.setCitizenId(citizenId);
                tempProfile.setFullName(fullName);
                tempProfile.setGender(gender);
                tempProfile.setNationality(nationality);
                tempProfile.setPhone(phone);
                tempProfile.setEmail(email);
                tempProfile.setResidence(residence);
                req.setAttribute("profile", tempProfile);
                req.getRequestDispatcher("/view/common/edit-profile.jsp").forward(req, resp);
                return;
            }
        }

        // Build updated profile model
        Profile updatedProfile = new Profile();
        updatedProfile.setCitizenId(citizenId);
        updatedProfile.setFullName(fullName.trim());
        updatedProfile.setDateOfBirth(dateOfBirth);
        updatedProfile.setGender(gender != null ? gender.trim() : null);
        updatedProfile.setNationality(nationality != null ? nationality.trim() : null);
        updatedProfile.setPhone(phone.trim());
        updatedProfile.setEmail(email.trim());
        updatedProfile.setResidence(residence != null ? residence.trim() : null);

        boolean isSuccess = profileService.updateProfile(updatedProfile);

        if (isSuccess) {
            resp.sendRedirect(req.getContextPath() + "/profile?success=true");
        } else {
            req.setAttribute("error", "Lỗi hệ thống khi cập nhật hồ sơ. Vui lòng thử lại sau.");
            req.setAttribute("profile", updatedProfile);
            req.getRequestDispatcher("/view/common/edit-profile.jsp").forward(req, resp);
        }
    }
}
