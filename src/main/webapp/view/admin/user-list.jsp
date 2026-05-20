<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh sách Tài khoản - HRM System</title>
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
            --danger-bg: #fef2f2;
            --success: #16a34a;
            --success-bg: #f0fdf4;
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

        /* ── Breadcrumb ── */
        .breadcrumb {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: .75rem;
            color: var(--muted);
            margin-bottom: 16px;
        }
        .breadcrumb a { color: var(--muted); text-decoration: none; transition: color .15s; }
        .breadcrumb a:hover { color: var(--text); }
        .breadcrumb span { font-size: .7rem; }

        .welcome {
            margin-bottom: 24px;
        }
        .welcome h1 { font-size: 1.4rem; font-weight: 600; letter-spacing: -.4px; }
        .welcome p  { font-size: .875rem; color: var(--muted); margin-top: 4px; }

        /* ── Card/Table Wrapper ── */
        .table-card {
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 1px 3px rgba(0,0,0,.02);
        }

        .table-header {
            padding: 16px 20px;
            border-bottom: 1px solid var(--border);
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .table-title { font-size: .9rem; font-weight: 600; color: var(--text); }

        .table-responsive {
            width: 100%;
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            text-align: left;
            font-size: .875rem;
        }

        th {
            background: #fafaf9;
            font-size: .75rem;
            text-transform: uppercase;
            letter-spacing: .05em;
            font-weight: 600;
            color: var(--muted);
            padding: 12px 20px;
            border-bottom: 1px solid var(--border);
        }

        td {
            padding: 14px 20px;
            border-bottom: 1px solid var(--border);
            color: var(--text);
            vertical-align: middle;
        }

        tr:last-child td { border-bottom: none; }
        tr:hover td { background: #fafaf9; }

        /* Status Badges */
        .status-badge {
            display: inline-flex;
            align-items: center;
            gap: 5px;
            font-size: .75rem;
            font-weight: 500;
            padding: 3px 8px;
            border-radius: 6px;
        }
        .status-active {
            background: var(--success-bg);
            color: var(--success);
        }
        .status-inactive {
            background: var(--danger-bg);
            color: var(--danger);
        }
        .status-dot {
            width: 6px; height: 6px;
            border-radius: 50%;
            background: currentColor;
        }

        /* Interactive Toggle Button */
        .btn-toggle {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            padding: 6px 12px;
            font-size: .75rem;
            font-weight: 500;
            border-radius: 6px;
            cursor: pointer;
            transition: all .15s ease;
            text-decoration: none;
            font-family: inherit;
        }
        .btn-deactivate {
            background: var(--danger-bg);
            color: var(--danger);
            border: 1px solid #fecaca;
        }
        .btn-deactivate:hover {
            background: var(--danger);
            color: #fff;
            border-color: var(--danger);
            box-shadow: 0 2px 4px rgba(220, 38, 38, 0.15);
        }
        .btn-activate {
            background: var(--success-bg);
            color: var(--success);
            border: 1px solid #bbf7d0;
        }
        .btn-activate:hover {
            background: var(--success);
            color: #fff;
            border-color: var(--success);
            box-shadow: 0 2px 4px rgba(22, 163, 74, 0.15);
        }
    </style>
</head>
<body>

<!-- Mock/Session Fallback -->
<c:set var="currentUser" value="${sessionScope.account != null ? sessionScope.account.username : 'Sơn'}" />
<c:set var="currentRoleId" value="${sessionScope.account != null ? sessionScope.account.roleId : 3}" />

<!-- ══ Sidebar ══════════════════════════════════════════ -->
<aside class="sidebar">
    <div class="sidebar-logo">
        <div class="logo-icon">HR</div>
        HRM System
    </div>

    <div class="nav-section">
        <div class="nav-label">Tổng quan</div>
        <a class="nav-item" href="${pageContext.request.contextPath}/view/common/home.jsp">
            <span class="nav-icon">⊞</span> Dashboard
        </a>
    </div>

    <c:choose>
        <c:when test="${currentRoleId == 3}">
            <div class="nav-section">
                <div class="nav-label">Quản trị</div>
                <a class="nav-item active" href="${pageContext.request.contextPath}/user-list"><span class="nav-icon">👤</span> Tài khoản</a>
                <a class="nav-item" href="${pageContext.request.contextPath}/role-list"><span class="nav-icon">🏷</span> Vai trò</a>
                <a class="nav-item" href="${pageContext.request.contextPath}/permission-list"><span class="nav-icon">🔐</span> Phân quyền</a>
            </div>
        </c:when>
        <c:when test="${currentRoleId == 2}">
            <div class="nav-section">
                <div class="nav-label">Nhân sự</div>
                <a class="nav-item active" href="${pageContext.request.contextPath}/user-list"><span class="nav-icon">👥</span> Tài khoản</a>
            </div>
        </c:when>
    </c:choose>

    <!-- User info + logout -->
    <div class="sidebar-footer">
        <div class="user-card">
            <div class="avatar">${currentUser.substring(0,1).toUpperCase()}</div>
            <div class="user-info">
                <div class="user-name">${currentUser}</div>
                <div class="user-role">
                    <c:choose>
                        <c:when test="${currentRoleId == 3}">Admin Advanced</c:when>
                        <c:when test="${currentRoleId == 2}">Admin</c:when>
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
        <span class="page-title">Quản lý Tài khoản</span>
        <span class="role-badge
            <c:choose>
                <c:when test="${currentRoleId == 3}">badge-advanced</c:when>
                <c:when test="${currentRoleId == 2}">badge-admin</c:when>
                <c:otherwise>badge-common</c:otherwise>
            </c:choose>">
            <c:choose>
                <c:when test="${currentRoleId == 3}">Admin Advanced</c:when>
                <c:when test="${currentRoleId == 2}">Admin</c:when>
                <c:otherwise>Common</c:otherwise>
            </c:choose>
        </span>
    </div>

    <div class="content">
        <!-- Breadcrumb -->
        <nav class="breadcrumb">
            <a href="${pageContext.request.contextPath}/view/common/home.jsp">Home</a>
            <span>/</span>
            <span>Tài khoản</span>
        </nav>

        <div class="welcome">
            <h1>Quản trị Tài khoản hệ thống 👥</h1>
            <p>Cho phép kích hoạt hoặc tạm khóa quyền truy cập của người dùng trong hệ thống HRM.</p>
        </div>

        <div class="table-card">
            <div class="table-header">
                <div class="table-title">Tất cả tài khoản</div>
            </div>

            <div class="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th>Mã ID</th>
                            <th>Tên đăng nhập</th>
                            <th>Mã định danh</th>
                            <th>Vai trò</th>
                            <th>Trạng thái</th>
                            <th style="text-align: right;">Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="user" items="${accounts}">
                            <tr>
                                <td><strong>#${user.accountId}</strong></td>
                                <td>
                                    <span style="font-weight: 500;">${user.username}</span>
                                </td>
                                <td>
                                    <span style="font-family: 'DM Mono', monospace; font-size: .8rem; color: var(--muted);">
                                        <c:choose>
                                            <c:when test="${not empty user.citizenId}">${user.citizenId}</c:when>
                                            <c:otherwise>N/A</c:otherwise>
                                        </c:choose>
                                    </span>
                                </td>
                                <td>
                                    <c:set var="roleName" value="${roleMap[user.roleId]}" />
                                    <span class="role-badge 
                                        <c:choose>
                                            <c:when test="${user.roleId == 3}">badge-advanced</c:when>
                                            <c:when test="${user.roleId == 2}">badge-admin</c:when>
                                            <c:otherwise>badge-common</c:otherwise>
                                        </c:choose>">
                                        <c:choose>
                                            <c:when test="${not empty roleName}">${roleName}</c:when>
                                            <c:otherwise>Role ${user.roleId}</c:otherwise>
                                        </c:choose>
                                    </span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${user.isActive}">
                                            <span class="status-badge status-active">
                                                <span class="status-dot"></span>
                                                Đang hoạt động
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-badge status-inactive">
                                                <span class="status-dot"></span>
                                                Bị khóa
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="text-align: right;">
                                    <c:choose>
                                        <c:when test="${user.isActive}">
                                            <a href="${pageContext.request.contextPath}/user-toggle?id=${user.accountId}" class="btn-toggle btn-deactivate">
                                                Khóa tài khoản
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${pageContext.request.contextPath}/user-toggle?id=${user.accountId}" class="btn-toggle btn-activate">
                                                Kích hoạt
                                            </a>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

</body>
</html>
