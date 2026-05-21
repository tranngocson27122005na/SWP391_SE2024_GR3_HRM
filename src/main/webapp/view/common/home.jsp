<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HRM System</title>
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600&family=DM+Mono:wght@400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/home.css">

</head>
<body>
<jsp:include page="/fragment/top-nav.jsp" />
<!-- ══ Sidebar ══════════════════════════════════════════ -->
<aside class="sidebar">
    <div class="sidebar-logo">
        <div class="logo-icon">HR</div>
        HRM System
    </div>

    <!-- Menu chung cho tất cả role -->
    <div class="nav-section">
        <div class="nav-label">Tổng quan</div>
        <a class="nav-item active" href="${pageContext.request.contextPath}/view/common/home.jsp">
            <span class="nav-icon">⊞</span> Dashboard
        </a>
        <a class="nav-item" href="${pageContext.request.contextPath}/profile">
            <span class="nav-icon">◎</span> Hồ sơ cá nhân
        </a>
        <a class="nav-item" href="${pageContext.request.contextPath}/change-password">
            <span class="nav-icon">🔑</span> Đổi mật khẩu
        </a>
    </div>

    <!-- Menu theo role -->
    <c:choose>

        <%-- ── Admin Advanced (3) ── --%>
        <c:when test="${account.roleId == 3}">
            <div class="nav-section">
                <div class="nav-label">Quản trị</div>
                <a class="nav-item" href="${pageContext.request.contextPath}/user-list"><span class="nav-icon">👤</span> Tài khoản</a>
                <a class="nav-item" href="${pageContext.request.contextPath}/role-list"><span class="nav-icon">🏷</span> Vai trò</a>
                <a class="nav-item" href="${pageContext.request.contextPath}/permission-list"><span class="nav-icon">🔐</span> Phân quyền</a>
            </div>
            <div class="nav-section">
                <div class="nav-label">Nhân sự</div>
                <a class="nav-item" href="#"><span class="nav-icon">👥</span> Nhân viên</a>
                <a class="nav-item" href="#"><span class="nav-icon">📋</span> Chấm công</a>
                <a class="nav-item" href="#"><span class="nav-icon">💰</span> Lương thưởng</a>
                <a class="nav-item" href="#"><span class="nav-icon">📊</span> Báo cáo</a>
            </div>
            <div class="nav-section">
                <div class="nav-label">Hệ thống</div>
                <a class="nav-item" href="#"><span class="nav-icon">⚙</span> Cài đặt</a>
                <a class="nav-item" href="#"><span class="nav-icon">📜</span> Nhật ký</a>
            </div>
        </c:when>

        <%-- ── Admin (2) ── --%>
        <c:when test="${account.roleId == 2}">
            <div class="nav-section">
                <div class="nav-label">Nhân sự</div>
                <a class="nav-item" href="#"><span class="nav-icon">👥</span> Nhân viên</a>
                <a class="nav-item" href="#"><span class="nav-icon">📋</span> Chấm công</a>
                <a class="nav-item" href="#"><span class="nav-icon">💰</span> Lương thưởng</a>
            </div>
            <div class="nav-section">
                <div class="nav-label">Báo cáo</div>
                <a class="nav-item" href="#"><span class="nav-icon">📊</span> Thống kê</a>
            </div>
        </c:when>

        <%-- ── Common (1) ── --%>
        <c:otherwise>
            <div class="nav-section">
                <div class="nav-label">Cá nhân</div>
                <a class="nav-item" href="#"><span class="nav-icon">📋</span> Chấm công</a>
                <a class="nav-item" href="#"><span class="nav-icon">📅</span> Lịch nghỉ phép</a>
                <a class="nav-item" href="#"><span class="nav-icon">💳</span> Bảng lương</a>
            </div>
        </c:otherwise>

    </c:choose>

    <!-- User info + logout -->
    <div class="sidebar-footer">
        <div class="user-card">
            <div class="avatar">${account.username.substring(0,1).toUpperCase()}</div>
            <div class="user-info">
                <div class="user-name">${account.username}</div>
                <div class="user-role">
                    <c:choose>
                        <c:when test="${account.roleId == 3}">Admin Advanced</c:when>
                        <c:when test="${account.roleId == 2}">Admin</c:when>
                        <c:otherwise>Common</c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        <a href="${pageContext.request.contextPath}/logout" class="btn-logout">
            ⟵ Đăng xuất
        </a>
    </div>
</aside>

<!-- ══ Main ════════════════════════════════════════════ -->
<div class="main">
    <div class="topbar">
        <span class="page-title">Dashboard</span>
        <span class="role-badge
            <c:choose>
                <c:when test="${account.roleId == 3}">badge-advanced</c:when>
                <c:when test="${account.roleId == 2}">badge-admin</c:when>
                <c:otherwise>badge-common</c:otherwise>
            </c:choose>">
            <c:choose>
                <c:when test="${account.roleId == 3}">Admin Advanced</c:when>
                <c:when test="${account.roleId == 2}">Admin</c:when>
                <c:otherwise>Common</c:otherwise>
            </c:choose>
        </span>
    </div>

    <div class="content">
        <div class="welcome">
            <h1>Xin chào, ${account.username} 👋</h1>
            <p>Hôm nay là ngày
                <%
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEEE, dd/MM/yyyy", new java.util.Locale("vi","VN"));
                    out.print(sdf.format(new java.util.Date()));
                %>
            </p>
        </div>

        <c:choose>

            <%-- ══ ADMIN ADVANCED ══ --%>
            <c:when test="${account.roleId == 3}">
                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-label">Tổng nhân viên</div>
                        <div class="stat-value">128</div>
                        <div class="stat-sub stat-up">↑ 3 tháng này</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-label">Đang chờ duyệt</div>
                        <div class="stat-value">7</div>
                        <div class="stat-sub">Nghỉ phép / tăng ca</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-label">Tổng tài khoản</div>
                        <div class="stat-value">132</div>
                        <div class="stat-sub stat-down">↓ 2 bị khóa</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-label">Vai trò hệ thống</div>
                        <div class="stat-value">3</div>
                        <div class="stat-sub">Common · Admin · Adv</div>
                    </div>
                </div>

                <div class="section-title">Quản trị hệ thống</div>
                <div class="card-grid">
                    <div class="action-card" onclick="location.href='${pageContext.request.contextPath}/permission-list'">
                        <div class="action-icon icon-purple">🔐</div>
                        <div class="action-name">Phân quyền</div>
                        <div class="action-desc">Cấu hình permission theo role</div>
                    </div>
                    <div class="action-card" onclick="location.href='${pageContext.request.contextPath}/user-list'">
                        <div class="action-icon icon-blue">👤</div>
                        <div class="action-name">Tài khoản</div>
                        <div class="action-desc">Tạo / khóa / reset tài khoản</div>
                    </div>
                    <div class="action-card" onclick="location.href='${pageContext.request.contextPath}/role-list'">
                        <div class="action-icon icon-teal">🏷</div>
                        <div class="action-name">Vai trò</div>
                        <div class="action-desc">Quản lý role hệ thống</div>
                    </div>
                    <div class="action-card">
                        <div class="action-icon icon-red">📜</div>
                        <div class="action-name">Nhật ký</div>
                        <div class="action-desc">Audit log toàn hệ thống</div>
                    </div>
                </div>

                <div class="section-title">Nhân sự</div>
                <div class="card-grid">
                    <div class="action-card">
                        <div class="action-icon icon-green">👥</div>
                        <div class="action-name">Nhân viên</div>
                        <div class="action-desc">Danh sách & hồ sơ</div>
                    </div>
                    <div class="action-card">
                        <div class="action-icon icon-amber">📊</div>
                        <div class="action-name">Báo cáo</div>
                        <div class="action-desc">Thống kê tổng hợp</div>
                    </div>
                    <div class="action-card">
                        <div class="action-icon icon-blue">💰</div>
                        <div class="action-name">Lương thưởng</div>
                        <div class="action-desc">Bảng lương toàn công ty</div>
                    </div>
                </div>
            </c:when>

            <%-- ══ ADMIN ══ --%>
            <c:when test="${account.roleId == 2}">
                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-label">Nhân viên quản lý</div>
                        <div class="stat-value">45</div>
                        <div class="stat-sub stat-up">↑ 1 tháng này</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-label">Chờ duyệt</div>
                        <div class="stat-value">4</div>
                        <div class="stat-sub">Đơn nghỉ phép</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-label">Điểm danh hôm nay</div>
                        <div class="stat-value">41</div>
                        <div class="stat-sub stat-down">↓ 4 vắng mặt</div>
                    </div>
                </div>

                <div class="section-title">Chức năng</div>
                <div class="card-grid">
                    <div class="action-card">
                        <div class="action-icon icon-green">👥</div>
                        <div class="action-name">Nhân viên</div>
                        <div class="action-desc">Xem & cập nhật hồ sơ</div>
                    </div>
                    <div class="action-card">
                        <div class="action-icon icon-blue">📋</div>
                        <div class="action-name">Chấm công</div>
                        <div class="action-desc">Duyệt bảng chấm công</div>
                    </div>
                    <div class="action-card">
                        <div class="action-icon icon-amber">💰</div>
                        <div class="action-name">Lương thưởng</div>
                        <div class="action-desc">Tính & xuất bảng lương</div>
                    </div>
                    <div class="action-card">
                        <div class="action-icon icon-teal">📊</div>
                        <div class="action-name">Thống kê</div>
                        <div class="action-desc">Báo cáo phòng ban</div>
                    </div>
                </div>
            </c:when>

            <%-- ══ COMMON ══ --%>
            <c:otherwise>
                <div class="section-title">Thông tin tài khoản</div>
                <div class="info-panel" style="margin-bottom: 24px;">
                    <div class="info-row">
                        <span class="info-key">Tên đăng nhập</span>
                        <span>${account.username}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-key">Mã nhân viên</span>
                        <span>#${account.accountId}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-key">Trạng thái</span>
                        <span style="color: #16a34a; font-weight: 500;">● Đang hoạt động</span>
                    </div>
                </div>

                <div class="section-title">Chức năng</div>
                <div class="card-grid">
                    <div class="action-card">
                        <div class="action-icon icon-blue">📋</div>
                        <div class="action-name">Chấm công</div>
                        <div class="action-desc">Xem bảng chấm công cá nhân</div>
                    </div>
                    <div class="action-card">
                        <div class="action-icon icon-green">📅</div>
                        <div class="action-name">Nghỉ phép</div>
                        <div class="action-desc">Đăng ký & xem lịch nghỉ</div>
                    </div>
                    <div class="action-card">
                        <div class="action-icon icon-amber">💳</div>
                        <div class="action-name">Bảng lương</div>
                        <div class="action-desc">Xem lương hàng tháng</div>
                    </div>
                </div>
            </c:otherwise>

        </c:choose>
    </div>
</div>

</body>
</html>
