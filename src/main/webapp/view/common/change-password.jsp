<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đổi mật khẩu | HRM System</title>
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
            --card-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
            --transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
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
            transition: var(--transition);
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
            transition: var(--transition);
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

        /* ── Change Password Form UI ── */
        .form-container {
            max-width: 520px;
            margin: 20px auto 0;
            display: flex;
            flex-direction: column;
            gap: 20px;
        }

        .form-card {
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: 12px;
            box-shadow: var(--card-shadow);
            padding: 32px 28px;
        }

        .form-header {
            text-align: center;
            margin-bottom: 28px;
        }

        .form-header-icon {
            font-size: 2rem;
            color: var(--accent);
            background: var(--accent-bg);
            width: 52px;
            height: 52px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 12px;
        }

        .form-header h2 {
            font-size: 1.25rem;
            font-weight: 600;
            color: var(--text);
        }

        .form-header p {
            font-size: 0.825rem;
            color: var(--muted);
            margin-top: 4px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
            gap: 6px;
            margin-bottom: 20px;
        }

        .form-group label {
            font-size: 0.825rem;
            font-weight: 600;
            color: var(--text);
        }

        .form-input {
            font-family: 'DM Sans', sans-serif;
            font-size: 0.875rem;
            padding: 10px 14px;
            border: 1px solid var(--border);
            border-radius: 8px;
            background: var(--bg);
            color: var(--text);
            outline: none;
            transition: var(--transition);
        }

        .form-input:focus {
            border-color: var(--accent);
            background: var(--surface);
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
        }

        .btn-submit {
            font-family: 'DM Sans', sans-serif;
            font-size: 0.875rem;
            font-weight: 600;
            color: #ffffff;
            background: var(--accent);
            border: none;
            border-radius: 8px;
            padding: 12px;
            cursor: pointer;
            width: 100%;
            transition: var(--transition);
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            box-shadow: 0 2px 6px rgba(37, 99, 235, 0.15);
        }

        .btn-submit:hover {
            background: #1d4ed8;
            box-shadow: 0 4px 12px rgba(37, 99, 235, 0.25);
        }

        /* Banners for feedback */
        .alert-banner {
            border-radius: 8px;
            padding: 12px 16px;
            font-size: 0.825rem;
            line-height: 1.5;
            display: flex;
            align-items: flex-start;
            gap: 10px;
            margin-bottom: 20px;
        }
        
        .alert-danger {
            background: #fef2f2;
            color: var(--danger);
            border: 1px solid #fecaca;
        }

        .alert-success {
            background: #f0fdf4;
            color: var(--success);
            border: 1px solid #bbf7d0;
        }

        .alert-icon {
            font-size: 1rem;
            flex-shrink: 0;
        }

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
        <a class="nav-item" href="${pageContext.request.contextPath}/view/common/home.jsp">
            <span class="nav-icon">⊞</span> Dashboard
        </a>
        <a class="nav-item" href="${pageContext.request.contextPath}/profile">
            <span class="nav-icon">◎</span> Hồ sơ cá nhân
        </a>
        <a class="nav-item active" href="${pageContext.request.contextPath}/change-password">
            <span class="nav-icon">🔑</span> Đổi mật khẩu
        </a>
    </div>

    <!-- Menu theo role -->
    <c:choose>
        <%-- ── Admin Advanced (3) ── --%>
        <c:when test="${account.roleId == 3}">
            <div class="nav-section">
                <div class="nav-label">Quản trị</div>
                <a class="nav-item" href="#"><span class="nav-icon">👤</span> Tài khoản</a>
                <a class="nav-item" href="#"><span class="nav-icon">🏷</span> Vai trò</a>
                <a class="nav-item" href="#"><span class="nav-icon">🔐</span> Phân quyền</a>
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
        <a href="${pageContext.request.contextPath}/view/common/logout.jsp" class="btn-logout">
            ⟵ Đăng xuất
        </a>
    </div>
</aside>

<!-- ══ Main ════════════════════════════════════════════ -->
<div class="main">
    <div class="topbar">
        <span class="page-title">Đổi mật khẩu</span>
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
        <div class="form-container">

            <div class="form-card">
                <div class="form-header">
                    <div class="form-header-icon">🔐</div>
                    <h2>Đổi mật khẩu tài khoản</h2>
                    <p>Vui lòng điền đầy đủ các thông tin bên dưới để cập nhật mật khẩu mới.</p>
                </div>

                <!-- Alert Banners -->
                <c:if test="${not empty error}">
                    <div class="alert-banner alert-danger">
                        <span class="alert-icon">⚠</span>
                        <span>${error}</span>
                    </div>
                </c:if>

                <c:if test="${not empty success}">
                    <div class="alert-banner alert-success">
                        <span class="alert-icon">✔</span>
                        <span>${success}</span>
                    </div>
                </c:if>

                <!-- Form -->
                <form action="${pageContext.request.contextPath}/change-password" method="post">
                    
                    <div class="form-group">
                        <label for="oldPassword">Mật khẩu hiện tại</label>
                        <input type="password" id="oldPassword" name="oldPassword" class="form-input" placeholder="Nhập mật khẩu hiện tại của bạn" required />
                    </div>

                    <div class="form-group">
                        <label for="newPassword">Mật khẩu mới</label>
                        <input type="password" id="newPassword" name="newPassword" class="form-input" placeholder="Nhập mật khẩu mới" required />
                    </div>

                    <div class="form-group" style="margin-bottom: 28px;">
                        <label for="confirmPassword">Xác nhận mật khẩu mới</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" class="form-input" placeholder="Xác nhận lại mật khẩu mới" required />
                    </div>

                    <button type="submit" class="btn-submit">
                        🔑 Cập nhật mật khẩu
                    </button>
                </form>
            </div>

        </div>
    </div>
</div>

</body>
</html>
