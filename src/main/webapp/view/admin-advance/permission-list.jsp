<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh sách Quyền hạn - HRM System</title>
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
            --success-bg: #f0fdf4;
            --purple: #7e22ce;
            --purple-bg: #fdf4ff;
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
            gap: 16px;
            flex-wrap: wrap;
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

        /* Custom Dropdown Styling */
        select {
            padding: 8px 16px;
            border-radius: 8px;
            border: 1px solid var(--border);
            font-family: inherit;
            font-size: .875rem;
            color: var(--text);
            background: var(--surface);
            cursor: pointer;
            outline: none;
            transition: border-color .15s;
        }
        select:focus {
            border-color: var(--accent);
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
                <a class="nav-item" href="${pageContext.request.contextPath}/user-list"><span class="nav-icon">👤</span> Tài khoản</a>
                <a class="nav-item" href="${pageContext.request.contextPath}/role-list"><span class="nav-icon">🏷</span> Vai trò</a>
                <a class="nav-item active" href="${pageContext.request.contextPath}/permission-list"><span class="nav-icon">🔐</span> Phân quyền</a>
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
        <span class="page-title">Quản lý Phân quyền</span>
        <span class="role-badge badge-advanced">Admin Advanced</span>
    </div>

    <div class="content">
        <!-- Breadcrumb -->
        <nav class="breadcrumb">
            <a href="${pageContext.request.contextPath}/view/common/home.jsp">Home</a>
            <span>/</span>
            <a href="${pageContext.request.contextPath}/role-list">Vai trò</a>
            <span>/</span>
            <span>Phân quyền</span>
        </nav>

        <div class="welcome">
            <h1>Phân quyền Hệ thống 🔐</h1>
            <p>Kiểm soát chi tiết các chức năng mà từng vai trò cụ thể được phép thực thi.</p>
        </div>

        <div class="table-card">
            <div class="table-header">
                <div class="table-title">
                    <c:choose>
                        <c:when test="${not empty selectedRoleId}">
                            Danh sách quyền của vai trò: <strong>${roleMap[selectedRoleId]}</strong>
                        </c:when>
                        <c:otherwise>Tất cả quyền hệ thống</c:otherwise>
                    </c:choose>
                </div>
                <div>
                    <label for="roleFilter" style="font-size: .8rem; color: var(--muted); margin-right: 8px; font-weight: 500;">Lọc theo vai trò:</label>
                    <select id="roleFilter" onchange="filterPermissions()">
                        <option value="">-- Tất cả vai trò --</option>
                        <c:forEach var="r" items="${roles}">
                            <option value="${r.roleId}" ${selectedRoleId == r.roleId ? 'selected' : ''}>${r.roleName}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th>Mã ID</th>
                            <th>Tên Quyền hạn (Permission)</th>
                            <th>Mô tả chức năng</th>
                            <th>Áp dụng cho vai trò</th>
                            <th style="text-align: right;">Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="permission" items="${permissions}">
                            <tr>
                                <td><strong>#${permission.permissionId}</strong></td>
                                <td>
                                    <span style="font-family: 'DM Mono', monospace; font-size: .85rem; font-weight: 500; color: var(--accent); background: var(--accent-bg); padding: 4px 10px; border-radius: 6px;">
                                        ${permission.permissionName}
                                    </span>
                                </td>
                                <td>
                                    <span style="color: var(--muted); font-size: .85rem;">${permission.description}</span>
                                </td>
                                <td>
                                    <c:set var="permRoleName" value="${roleMap[permission.roleId]}" />
                                    <span class="role-badge 
                                        <c:choose>
                                            <c:when test="${permission.roleId == 3}">badge-advanced</c:when>
                                            <c:when test="${permission.roleId == 2}">badge-admin</c:when>
                                            <c:otherwise>badge-common</c:otherwise>
                                        </c:choose>">
                                        <c:choose>
                                            <c:when test="${not empty permRoleName}">${permRoleName}</c:when>
                                            <c:otherwise>Role ${permission.roleId}</c:otherwise>
                                        </c:choose>
                                    </span>
                                </td>
                                <td style="text-align: right;">
                                    <a href="${pageContext.request.contextPath}/edit-permission?permissionId=${permission.permissionId}" class="btn-action">
                                        ✏️ Edit
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty permissions}">
                            <tr>
                                <td colspan="4" style="text-align: center; color: var(--muted); padding: 30px;">
                                    Không tìm thấy quyền hạn nào cho vai trò này.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<script>
    function filterPermissions() {
        const roleId = document.getElementById('roleFilter').value;
        window.location.href = '${pageContext.request.contextPath}/permission-list' + (roleId ? '?roleId=' + roleId : '');
    }
</script>

</body>
</html>
