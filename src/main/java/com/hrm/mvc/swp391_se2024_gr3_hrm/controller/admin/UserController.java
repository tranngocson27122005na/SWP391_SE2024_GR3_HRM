package com.hrm.mvc.swp391_se2024_gr3_hrm.controller.admin;

import com.hrm.mvc.swp391_se2024_gr3_hrm.dto.AccountWithProfile;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Profile;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Role;
import com.hrm.mvc.swp391_se2024_gr3_hrm.service.AccountService;
import com.hrm.mvc.swp391_se2024_gr3_hrm.service.RoleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/admin/user")
public class UserController extends HttpServlet {

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    private AccountService accountService;
    private RoleService roleService;

    @Override
    public void init() {
        accountService = new AccountService();
        roleService = new RoleService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            action = "detail";
        }

        if ("list".equals(action)) {
            resp.sendRedirect(req.getContextPath() + "/admin/user-list");
            return;
        }

        if ("detail".equals(action)) {
            showDetail(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/admin/user-list");
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        int id = parseInt(idParam, -1);

        List<Role> activeRoles = roleService.getActiveRoles();
        req.setAttribute("activeRoles", activeRoles);

        if (id <= 0) {
            req.getRequestDispatcher("/view/admin/user-detail.jsp").forward(req, resp);
            return;
        }

        AccountWithProfile detail = accountService.getAccountWithProfileById(id);
        if (detail == null) {
            req.setAttribute("error", "Không tìm thấy tài khoản.");
            req.getRequestDispatcher("/view/admin/user-detail.jsp").forward(req, resp);
            return;
        }

        req.setAttribute("accountDetail", detail);
        req.getRequestDispatcher("/view/admin/user-detail.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String accountIdParam = req.getParameter("accountId");
        String isActiveParam = req.getParameter("isActive");
        String action = req.getParameter("action");

        if (accountIdParam != null && isActiveParam != null && action == null) {
            handleToggleActive(req, resp, accountIdParam, isActiveParam);
            return;
        }

        if ("add".equals(action)) {
            handleAdd(req, resp);
            return;
        }

        if ("update".equals(action)) {
            handleUpdate(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/admin/user-list");
    }

    private void handleToggleActive(HttpServletRequest req, HttpServletResponse resp,
                                    String accountIdParam, String isActiveParam) throws IOException {
        Integer accountId = parseInteger(accountIdParam);
        if (accountId == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/user-list?error=invalid");
            return;
        }
        boolean active = "true".equalsIgnoreCase(isActiveParam);
        accountService.toggleAccountActive(accountId, active);

        String keyword = req.getParameter("keyword");
        String page = req.getParameter("page");
        String redirect = buildListRedirect(req.getContextPath(), keyword, page, "success=toggle");
        resp.sendRedirect(redirect);
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Account account = buildAccountFromRequest(req, true);
        Profile profile = buildProfileFromRequest(req, true);
        String confirmPassword = req.getParameter("confirmPassword");

        String validationError = validateCreate(account, profile, confirmPassword);
        if (validationError != null) {
            req.setAttribute("error", validationError);
            req.setAttribute("accountDetail", toDto(account, profile));
            req.setAttribute("activeRoles", roleService.getActiveRoles());
            req.getRequestDispatcher("/view/admin/user-detail.jsp").forward(req, resp);
            return;
        }

        boolean created = accountService.createAccount(account, profile);
        if (!created) {
            req.setAttribute("error", "Không thể tạo tài khoản. Kiểm tra username, email, citizen ID hoặc vai trò.");
            req.setAttribute("accountDetail", toDto(account, profile));
            req.setAttribute("activeRoles", roleService.getActiveRoles());
            req.getRequestDispatcher("/view/admin/user-detail.jsp").forward(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/admin/user-list?success=created");
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Account account = buildAccountFromRequest(req, false);
        Profile profile = buildProfileFromRequest(req, false);

        String validationError = validateUpdate(account, profile);
        if (validationError != null) {
            req.setAttribute("error", validationError);
            req.setAttribute("accountDetail", toDto(account, profile));
            req.setAttribute("activeRoles", roleService.getActiveRoles());
            req.getRequestDispatcher("/view/admin/user-detail.jsp").forward(req, resp);
            return;
        }

        boolean updated = accountService.updateAccountAndProfile(account, profile);
        if (!updated) {
            req.setAttribute("error", "Không thể cập nhật tài khoản.");
            req.setAttribute("accountDetail", toDto(account, profile));
            req.setAttribute("activeRoles", roleService.getActiveRoles());
            req.getRequestDispatcher("/view/admin/user-detail.jsp").forward(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/admin/user?action=detail&id=" + account.getAccountId() + "&success=updated");
    }

    private String validateCreate(Account account, Profile profile, String confirmPassword) {
        if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
            return "Tên đăng nhập là bắt buộc.";
        }
        if (account.getPassword() == null || account.getPassword().isEmpty()) {
            return "Mật khẩu là bắt buộc.";
        }
        if (!account.getPassword().equals(confirmPassword)) {
            return "Xác nhận mật khẩu không khớp.";
        }
        if (account.getRoleId() == null) {
            return "Vai trò là bắt buộc.";
        }
        if (profile.getCitizenId() == null) {
            return "Mã công dân là bắt buộc.";
        }
        if (accountService.isUsernameTaken(account.getUsername(), null)) {
            return "Tên đăng nhập đã tồn tại.";
        }
        return null;
    }

    private String validateUpdate(Account account, Profile profile) {
        if (account.getAccountId() == null) {
            return "Mã tài khoản không hợp lệ.";
        }
        if (account.getRoleId() == null) {
            return "Vai trò là bắt buộc.";
        }
        if (profile.getFullName() == null || profile.getFullName().trim().isEmpty()) {
            return "Họ tên là bắt buộc.";
        }
        return null;
    }

    private Account buildAccountFromRequest(HttpServletRequest req, boolean isCreate) {
        Account account = new Account();
        account.setAccountId(parseInteger(req.getParameter("accountId")));
        account.setUsername(trim(req.getParameter("username")));
        if (isCreate) {
            account.setPassword(req.getParameter("password"));
        }
        account.setRoleId(parseInteger(req.getParameter("roleId")));
        return account;
    }

    private Profile buildProfileFromRequest(HttpServletRequest req, boolean isCreate) {
        Profile profile = new Profile();
        profile.setCitizenId(parseInteger(req.getParameter("citizenId")));
        profile.setFullName(trim(req.getParameter("fullName")));
        profile.setEmail(trim(req.getParameter("email")));
        profile.setPhone(trim(req.getParameter("phone")));
        profile.setAddress(trim(req.getParameter("address")));

        String dob = trim(req.getParameter("dateOfBirth"));
        if (dob != null && !dob.isEmpty()) {
            try {
                profile.setDateOfBirth(new SimpleDateFormat(DATE_PATTERN).parse(dob));
            } catch (ParseException ignored) {
            }
        }

        String gender = req.getParameter("gender");
        if (gender != null && !gender.isEmpty()) {
            profile.setGender("1".equals(gender) || "true".equalsIgnoreCase(gender));
        }

        String married = req.getParameter("married");
        if (married != null && !married.isEmpty()) {
            profile.setMarried("1".equals(married) || "true".equalsIgnoreCase(married));
        }

        if (!isCreate && profile.getCitizenId() == null) {
            profile.setCitizenId(parseInteger(req.getParameter("hiddenCitizenId")));
        }
        return profile;
    }

    private AccountWithProfile toDto(Account account, Profile profile) {
        AccountWithProfile dto = new AccountWithProfile();
        if (account != null) {
            dto.setAccountId(account.getAccountId());
            dto.setUsername(account.getUsername());
            dto.setRoleId(account.getRoleId());
            dto.setIsActive(account.getIsActive());
        }
        if (profile != null) {
            dto.setCitizenId(profile.getCitizenId());
            dto.setFullName(profile.getFullName());
            dto.setEmail(profile.getEmail());
            dto.setPhone(profile.getPhone());
            dto.setAddress(profile.getAddress());
            dto.setDateOfBirth(profile.getDateOfBirth());
            dto.setGender(profile.getGender());
            dto.setMarried(profile.getMarried());
        }
        return dto;
    }

    private String buildListRedirect(String contextPath, String keyword, String page, String success) {
        StringBuilder sb = new StringBuilder(contextPath).append("/admin/user-list?");
        if (keyword != null && !keyword.isEmpty()) {
            sb.append("keyword=").append(encode(keyword)).append("&");
        }
        if (page != null && !page.isEmpty()) {
            sb.append("page=").append(page).append("&");
        }
        sb.append(success);
        return sb.toString();
    }

    private String encode(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int parseInt(String value, int defaultValue) {
        Integer parsed = parseInteger(value);
        return parsed == null ? defaultValue : parsed;
    }
}
