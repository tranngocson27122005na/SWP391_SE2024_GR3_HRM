package com.hrm.mvc.swp391_se2024_gr3_hrm.controller.admin;

import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account;
import com.hrm.mvc.swp391_se2024_gr3_hrm.service.AccountService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "UserController", value = "/admin/user")
public class UserController extends HttpServlet {
    private final AccountService accountService = new AccountService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Ép mã hóa tiếng Việt UTF-8 cục bộ cho luồng Request/Response
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String action = req.getParameter("action");

        // --- CHỨC NĂNG 7: XEM DANH SÁCH TÀI KHOẢN (CÓ PHÂN TRANG) ---
        if (action == null || action.equals("list")) {
            int page = 1;
            int pageSize = 5; // Số lượng dòng trên một trang

            if (req.getParameter("page") != null) {
                try {
                    page = Integer.parseInt(req.getParameter("page"));
                    if (page < 1) page = 1;
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }

            // Gọi tầng Service lấy dữ liệu phân trang từ Database
            List<Account> list = accountService.getAllAccounts(page, pageSize);
            req.setAttribute("userList", list);
            req.setAttribute("currentPage", page);

            // ĐƯỜNG DẪN TUYỆT ĐỐI CHUẨN (Có dấu / ở đầu để tính từ gốc webapp)
            req.getRequestDispatcher("/view/admin/user-list.jsp").forward(req, resp);

            // --- CHỨC NĂNG 8: XEM CHI TIẾT TÀI KHOẢN ---
        } else if (action.equals("detail")) {
            int id = 0;
            try {
                if (req.getParameter("id") != null && !req.getParameter("id").isEmpty()) {
                    id = Integer.parseInt(req.getParameter("id"));
                }
            } catch (NumberFormatException e) {
                resp.sendRedirect(req.getContextPath() + "/admin/user?action=list");
                return;
            }

            // Nếu id > 0 -> Bấm xem chi tiết một user cụ thể đang tồn tại
            if (id > 0) {
                Account acc = accountService.getAccountById(id);
                req.setAttribute("accountDetail", acc);
            }
            // Nếu id == 0 -> accountDetail sẽ null -> Giao diện tự render thành Form Thêm mới trống (Chức năng 9)

            // ĐƯỜNG DẪN TUYỆT ĐỐI CHUẨN (Có dấu / ở đầu để sửa lỗi 404)
            req.getRequestDispatcher("/view/admin/user-detail.jsp").forward(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/user?action=list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Ép mã hóa UTF-8 để không bị lỗi font khi nhận dữ liệu có dấu từ Form gửi lên
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String action = req.getParameter("action");

        // --- CHỨC NĂNG 9: XỬ LÝ LƯU THÊM MỚI TÀI KHOẢN ---
        if ("add".equals(action)) {
            Account newAcc = new Account();
            newAcc.setUsername(req.getParameter("username"));
            newAcc.setPassword(req.getParameter("password"));
            newAcc.setIsActive(true);

            try {
                newAcc.setRoleId(Integer.parseInt(req.getParameter("roleId")));

                // Kiểm tra và gán thuộc tính citizenId (Khớp theo model Account của nhóm bạn)
                if (req.getParameter("citizenId") != null && !req.getParameter("citizenId").isEmpty()) {
                    newAcc.setCitizenId(Integer.parseInt(req.getParameter("citizenId")));
                }
            } catch (NumberFormatException e) {
                req.setAttribute("error", "Lỗi: Mã vai trò hoặc mã công dân không hợp lệ!");
                req.getRequestDispatcher("/view/admin/user-detail.jsp").forward(req, resp);
                return;
            }

            // Gọi Service thực hiện câu lệnh INSERT xuống MySQL thông qua MyBatis
            boolean isSuccess = accountService.createAccount(newAcc);
            if (isSuccess) {
                // Thêm thành công -> Dùng Redirect về trang list để tránh bị lặp dữ liệu khi người dùng F5
                resp.sendRedirect(req.getContextPath() + "/admin/user?action=list");
            } else {
                // Thêm thất bại (Ví dụ: Trùng Username) -> Trả về lỗi hiển thị ngay trên Form
                req.setAttribute("error", "Lỗi: Không thể thêm tài khoản mới (Có thể trùng Username)!");
                req.getRequestDispatcher("/view/admin/user-detail.jsp").forward(req, resp);
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/user?action=list");
        }
    }
}