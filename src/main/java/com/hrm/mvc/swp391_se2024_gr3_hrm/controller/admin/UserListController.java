package com.hrm.mvc.swp391_se2024_gr3_hrm.controller.admin;

import com.hrm.mvc.swp391_se2024_gr3_hrm.dto.AccountWithProfile;
import com.hrm.mvc.swp391_se2024_gr3_hrm.service.AccountService;
import com.hrm.mvc.swp391_se2024_gr3_hrm.utility.presentation.Pagging;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/user-list")
public class UserListController extends HttpServlet {

    private static final int RECORDS_PER_PAGE = 10;

    private AccountService accountService;

    @Override
    public void init() {
        accountService = new AccountService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        if (keyword == null) {
            keyword = "";
        }

        int currentPage = 0;
        String pageParam = req.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Math.max(0, Integer.parseInt(pageParam) - 1);
            } catch (NumberFormatException ignored) {
            }
        }

        int totalRecords = accountService.countAccountsWithProfile(keyword);
        if (totalRecords == 0) {
            currentPage = 0;
        }
        Pagging pagging = new Pagging(totalRecords, RECORDS_PER_PAGE, currentPage);
        pagging.calc();
        if (pagging.getIndex() < 0) {
            pagging.setIndex(0);
        }

        List<AccountWithProfile> userList = accountService.getAllAccountsWithProfile(
                pagging.getStart(), RECORDS_PER_PAGE, keyword);

        req.setAttribute("userList", userList);
        req.setAttribute("pagging", pagging);
        req.setAttribute("keyword", keyword);

        String success = req.getParameter("success");
        if ("created".equals(success)) {
            req.setAttribute("message", "Tạo tài khoản thành công.");
        } else if ("toggle".equals(success)) {
            req.setAttribute("message", "Cập nhật trạng thái tài khoản thành công.");
        }

        String error = req.getParameter("error");
        if ("invalid".equals(error)) {
            req.setAttribute("error", "Yêu cầu không hợp lệ.");
        }

        req.getRequestDispatcher("/view/admin/user-list.jsp").forward(req, resp);
    }
}
