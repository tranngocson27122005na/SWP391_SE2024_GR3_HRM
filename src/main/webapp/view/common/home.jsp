<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HRM System</title>
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600&family=DM+Mono:wght@400;500&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        :root {
            --sidebar-w: 240px;
            --header-h: 56px;
            --bg: #f5f4f0;
            --surface: #ffffff;
            --border: #e2e0d8;
            --text: #1a1917;
            --muted: #6b6860;
            --accent: #2563eb;
            --accent-bg: #eff6ff;
            --danger: #dc2626;
            --success: #16a34a;
            --warning: #d97706;
        }

        body {
            font-family: 'DM Sans', sans-serif;
            background: var(--bg);
            color: var(--text);
            display: flex;
            min-height: 100vh;
        }

        /* ── Sidebar ── */
        .sidebar {
            width: var(--sidebar-w);
            min-height: 100vh;
            background: var(--surface);
            border-right: 1px solid var(--border);
            display: flex;
            flex-direction: column;
            position: fixed;
            top: 0; left: 0;
            z-index: 100;
        }

        .sidebar-logo {
            height: var(--header-h);
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 0 20px;
            border-bottom: 1px solid var(--border);
            font-weight: 600;
            font-size: 1rem;
            letter-spacing: -.3px;
        }

        .logo-icon {
            width: 28px; height: 28px;
            background: var(--text);
            border-radius: 6px;
            display: flex; align-items: center; justify-content: center;
            color: #fff;
            font-family: 'DM Mono', monospace;
            font-size: .75rem;
        }

        .nav-section {
            padding: 16px 12px 8px;
        }

        .nav-label {
            font-size: .7rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: .08em;
            color: var(--muted);
            padding: 0 8px;
            margin-bottom: 6px;
        }

        .nav-item {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 8px 10px;
            border-radius: 7px;
            font-size: .875rem;
            color: var(--muted);
            text-decoration: none;
            transition: background .15s, color .15s;
            cursor: pointer;
        }

        .nav-item:hover { background: var(--bg); color: var(--text); }
        .nav-item.active { background: var(--accent-bg); color: var(--accent); font-weight: 500; }

        .nav-icon { font-size: 1rem; width: 20px; text-align: center; }

        .sidebar-footer {
            margin-top: auto;
            padding: 16px;
            border-top: 1px solid var(--border);
        }

        .user-card {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 8px;
            border-radius: 8px;
        }

        .avatar {
            width: 32px; height: 32px;
            border-radius: 50%;
            background: var(--accent);
            color: #fff;
            display: flex; align-items: center; justify-content: center;
            font-size: .75rem;
            font-weight: 600;
            flex-shrink: 0;
        }

        .user-info { flex: 1; min-width: 0; }
        .user-name { font-size: .8rem; font-weight: 500; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
        .user-role { font-size: .7rem; color: var(--muted); }

        .btn-logout {
            display: flex;
            align-items: center;
            gap: 6px;
            margin-top: 10px;
            padding: 7px 10px;
            width: 100%;
            border: 1px solid var(--border);
            border-radius: 7px;
            background: transparent;
            font-size: .8rem;
            color: var(--muted);
            cursor: pointer;
            transition: all .15s;
            text-decoration: none;
            font-family: 'DM Sans', sans-serif;
        }
        .btn-logout:hover { border-color: var(--danger); color: var(--danger); background: #fef2f2; }

        /* ── Main ── */
        .main {
            margin-left: var(--sidebar-w);
            flex: 1;
            display: flex;
            flex-direction: column;
        }

        .topbar {
            height: var(--header-h);
            background: var(--surface);
            border-bottom: 1px solid var(--border);
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 28px;
            position: sticky;
            top: 0; z-index: 50;
        }

        .page-title { font-size: 1rem; font-weight: 500; }

        .role-badge {
            font-size: .7rem;
            font-weight: 600;
            padding: 3px 10px;
            border-radius: 20px;
            letter-spacing: .04em;
        }

        .badge-common   { background: #f0fdf4; color: #15803d; border: 1px solid #bbf7d0; }
        .badge-admin    { background: #eff6ff; color: #1d4ed8; border: 1px solid #bfdbfe; }
        .badge-advanced { background: #fdf4ff; color: #7e22ce; border: 1px solid #e9d5ff; }

        .content { padding: 28px; }

        .welcome {
            margin-bottom: 28px;
        }
        .welcome h1 { font-size: 1.4rem; font-weight: 600; letter-spacing: -.4px; }
        .welcome p  { font-size: .875rem; color: var(--muted); margin-top: 4px; }

        /* ── Stat cards ── */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
            gap: 14px;
            margin-bottom: 28px;
        }

        .stat-card {
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: 10px;
            padding: 18px 20px;
        }

        .stat-label { font-size: .75rem; color: var(--muted); font-weight: 500; text-transform: uppercase; letter-spacing: .06em; }
        .stat-value { font-size: 1.8rem; font-weight: 600; font-family: 'DM Mono', monospace; margin-top: 6px; letter-spacing: -1px; }
        .stat-sub   { font-size: .75rem; color: var(--muted); margin-top: 4px; }
        .stat-up    { color: var(--success); }
        .stat-down  { color: var(--danger); }

        /* ── Section ── */
        .section-title {
            font-size: .8rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: .07em;
            color: var(--muted);
            margin-bottom: 14px;
        }

        .card-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 12px;
            margin-bottom: 28px;
        }

        .action-card {
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: 10px;
            padding: 18px 20px;
            cursor: pointer;
            transition: border-color .15s, box-shadow .15s;
        }
        .action-card:hover { border-color: #a0aec0; box-shadow: 0 2px 8px rgba(0,0,0,.06); }

        .action-icon {
            width: 36px; height: 36px;
            border-radius: 8px;
            display: flex; align-items: center; justify-content: center;
            font-size: 1rem;
            margin-bottom: 12px;
        }

        .icon-blue   { background: #eff6ff; color: #2563eb; }
        .icon-green  { background: #f0fdf4; color: #16a34a; }
        .icon-amber  { background: #fffbeb; color: #d97706; }
        .icon-purple { background: #fdf4ff; color: #9333ea; }
        .icon-red    { background: #fef2f2; color: #dc2626; }
        .icon-teal   { background: #f0fdfa; color: #0d9488; }

        .action-name { font-size: .875rem; font-weight: 500; }
        .action-desc { font-size: .75rem; color: var(--muted); margin-top: 3px; }

        /* ── Info panel (Common user) ── */
        .info-panel {
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: 10px;
            padding: 20px 24px;
        }
        .info-row {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 10px 0;
            border-bottom: 1px solid var(--border);
            font-size: .875rem;
        }
        .info-row:last-child { border-bottom: none; }
        .info-key { width: 140px; color: var(--muted); font-size: .8rem; flex-shrink: 0; }
    </style>
</head>
<body>

<!-- ══ Sidebar ══════════════════════════════════════════ -->
<aside class="sidebar">
    <div class="sidebar-logo">
        <div class="logo-icon">HR</div>
        HRM System
    </div>

    <!-- Menu chung cho tất cả role -->
    <div class="nav-section">
        <div class="nav-label">Tổng quan</div>
        <a class="nav-item active" href="#">
            <span class="nav-icon">⊞</span> Dashboard
        </a>
        <a class="nav-item" href="#">
            <span class="nav-icon">◎</span> Hồ sơ cá nhân
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
