package com.hrm.controller.common;

import com.hrm.dto.response.HomeTile;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.security.PositionPermissionMatrix;
import com.hrm.infrastructure.security.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@WebServlet("/home")
public class HomeController extends HttpServlet {

    private static final String PERM_EMPLOYEE_LIST = "employee:READ";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSession user = SessionManager.getUserSession(request);
        request.setAttribute("user", user);
        request.setAttribute("tiles", buildTiles(user));
        request.getRequestDispatcher("/WEB-INF/views/common/home.jsp")
                .forward(request, response);
    }

    private List<HomeTile> buildTiles(UserSession user) {
        List<HomeTile> tiles = new ArrayList<>();
        if (user == null) {
            return tiles;
        }
        if (user.isAdmin()) {
            tiles.add(new HomeTile("sys-user", "Tài khoản", "/sys-user/list", "users"));
            tiles.add(new HomeTile("sys-user-provision", "Cấp TK nhân viên",
                    "/sys-user/provision-list", "provision"));
            tiles.add(new HomeTile("permission-matrix", "Ma trận phân quyền",
                    "/permission-matrix/list", "matrix"));
            return tiles;
        }
        if (user.isUser()) {
            if (user.getEmployeeId() != null) {
                tiles.add(new HomeTile("my-profile", "Hồ sơ cá nhân", "/employee/me", "profile"));
            }
            Set<String> perms = PositionPermissionMatrix.permissionsOf(user.getPositionId());
            if (perms.contains(PERM_EMPLOYEE_LIST)) {
                tiles.add(new HomeTile("employee", "Nhân viên", "/employee/list", "employee"));
            }
            if (perms.contains("attendance:IMPORT")) {
                tiles.add(new HomeTile("attendance-import", "Import chấm công",
                        "/attendance/import", "employee"));
            }
            if (perms.contains("attendance:READ")) {
                tiles.add(new HomeTile("attendance-list", "Summary chấm công",
                        "/attendance/list", "employee"));
            }
            if (perms.contains("payslip:READ")) {
                tiles.add(new HomeTile("payslip-list", "Phiếu lương", "/payslip/list", "matrix"));
            }
            if (perms.contains("payslip:CREATE")) {
                tiles.add(new HomeTile("payslip-create", "Chạy tính lương",
                        "/payslip/create", "provision"));
            }
            if (perms.contains("payslip:UPDATE")) {
                tiles.add(new HomeTile("payslip-edit", "Tham số lương",
                        "/payslip/edit", "users"));
            }
        }
        return tiles;
    }
}
