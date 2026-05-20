<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hồ sơ cá nhân | HRM System</title>
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

        /* ── Profile UI Styles ── */
        .profile-container {
            display: flex;
            flex-direction: column;
            gap: 24px;
            max-width: 1000px;
            margin: 0 auto;
        }

        .profile-header-card {
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: 12px;
            overflow: hidden;
            box-shadow: var(--card-shadow);
        }

        .profile-banner {
            height: 120px;
            background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
            border-bottom: 1px solid var(--border);
            position: relative;
        }

        .profile-header-content {
            padding: 24px 28px;
            display: flex;
            align-items: flex-end;
            gap: 24px;
            margin-top: -60px;
            position: relative;
            z-index: 10;
        }

        .profile-large-avatar {
            width: 110px;
            height: 110px;
            border-radius: 50%;
            background: var(--accent);
            color: #fff;
            font-size: 2.5rem;
            font-weight: 600;
            display: flex;
            align-items: center;
            justify-content: center;
            border: 4px solid var(--surface);
            box-shadow: 0 4px 10px rgba(0,0,0,0.06);
            flex-shrink: 0;
            text-transform: uppercase;
        }

        .profile-header-info {
            flex: 1;
            padding-bottom: 8px;
        }

        .profile-name-row {
            display: flex;
            align-items: center;
            gap: 12px;
            flex-wrap: wrap;
        }

        .profile-name-row h1 {
            font-size: 1.5rem;
            font-weight: 600;
            letter-spacing: -.4px;
            color: var(--text);
        }

        .profile-status-badge {
            font-size: 0.725rem;
            font-weight: 500;
            padding: 2px 8px;
            border-radius: 6px;
            display: inline-flex;
            align-items: center;
            gap: 4px;
        }
        .status-verified { background: #dcfce7; color: #15803d; border: 1px solid #bbf7d0; }
        .status-empty { background: #fef3c7; color: #d97706; border: 1px solid #fde68a; }

        .profile-sub-row {
            font-size: 0.875rem;
            color: var(--muted);
            margin-top: 6px;
            display: flex;
            gap: 16px;
            flex-wrap: wrap;
        }

        .profile-sub-row span {
            display: flex;
            align-items: center;
            gap: 5px;
        }

        /* Grid section for details */
        .profile-details-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(450px, 1fr));
            gap: 24px;
        }

        @media (max-width: 768px) {
            .profile-details-grid {
                grid-template-columns: 1fr;
            }
        }

        .details-card {
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: 12px;
            box-shadow: var(--card-shadow);
            padding: 24px;
            display: flex;
            flex-direction: column;
            gap: 18px;
        }

        .card-header {
            display: flex;
            align-items: center;
            gap: 10px;
            border-bottom: 1px solid var(--border);
            padding-bottom: 12px;
            margin-bottom: 4px;
        }

        .card-header-icon {
            font-size: 1.1rem;
            color: var(--accent);
            background: var(--accent-bg);
            width: 28px;
            height: 28px;
            border-radius: 6px;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .card-title {
            font-size: 0.95rem;
            font-weight: 600;
            color: var(--text);
        }

        .info-row {
            display: flex;
            align-items: center;
            gap: 16px;
            padding: 8px 0;
            border-bottom: 1px dotted var(--border);
            font-size: 0.875rem;
            transition: var(--transition);
        }
        .info-row:last-child { border-bottom: none; }
        .info-row:hover {
            background-color: #faf9f6;
            padding-left: 4px;
        }

        .info-key {
            width: 140px;
            color: var(--muted);
            font-size: 0.825rem;
            flex-shrink: 0;
            font-weight: 500;
        }

        .info-value {
            color: var(--text);
            font-weight: 400;
            word-break: break-all;
        }
        .info-value.highlight {
            font-family: 'DM Mono', monospace;
            font-weight: 500;
        }

        /* Alert Callout for empty profile */
        .empty-profile-callout {
            background: #fffbeb;
            border: 1px solid #fef3c7;
            border-radius: 12px;
            padding: 20px 24px;
            display: flex;
            gap: 16px;
            box-shadow: var(--card-shadow);
        }

        .callout-icon {
            font-size: 1.5rem;
            color: var(--warning);
            flex-shrink: 0;
        }

        .callout-content h3 {
            font-size: 0.925rem;
            font-weight: 600;
            color: #92400e;
            margin-bottom: 6px;
        }

        .callout-content p {
            font-size: 0.825rem;
            color: #b45309;
            line-height: 1.5;
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
        <a class="nav-item active" href="${pageContext.request.contextPath}/profile">
            <span class="nav-icon">◎</span> Hồ sơ cá nhân
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
        <span class="page-title">Hồ sơ cá nhân</span>
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
        <div class="profile-container">

            <!-- Check if profile details are loaded -->
            <c:choose>
                <c:when test="${not empty profile}">
                    
                    <!-- ══ PROFILE HEADER CARD ══ -->
                    <div class="profile-header-card">
                        <div class="profile-banner"></div>
                        <div class="profile-header-content">
                            <!-- Large Circle Avatar with Initial of Full Name -->
                            <div class="profile-large-avatar">
                                ${profile.fullName.substring(0,1)}
                            </div>
                            <div class="profile-header-info">
                                <div class="profile-name-row">
                                    <h1>${profile.fullName}</h1>
                                    <span class="profile-status-badge status-verified">
                                        ● Hồ sơ đã liên kết
                                    </span>
                                </div>
                                <div class="profile-sub-row">
                                    <span>
                                        <strong>Mã định danh:</strong> #${profile.citizenId}
                                    </span>
                                    <span>
                                        <strong>Tài khoản:</strong> @${account.username}
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- ══ PROFILE DETAILS GRID ══ -->
                    <div class="profile-details-grid">
                        
                        <!-- Card 1: Thông tin cơ bản -->
                        <div class="details-card">
                            <div class="card-header">
                                <span class="card-header-icon">👤</span>
                                <span class="card-title">Thông tin cơ bản</span>
                            </div>
                            
                            <div class="info-row">
                                <span class="info-key">Họ và tên</span>
                                <span class="info-value" style="font-weight: 500;">${profile.fullName}</span>
                            </div>
                            
                            <div class="info-row">
                                <span class="info-key">Ngày sinh</span>
                                <span class="info-value">
                                    <fmt:formatDate value="${profile.dateOfBirth}" pattern="dd/MM/yyyy" />
                                </span>
                            </div>
                            
                            <div class="info-row">
                                <span class="info-key">Giới tính</span>
                                <span class="info-value">${profile.gender}</span>
                            </div>
                            
                            <div class="info-row">
                                <span class="info-key">Quốc tịch</span>
                                <span class="info-value">${profile.nationality}</span>
                            </div>
                        </div>

                        <!-- Card 2: Liên hệ & Cư trú -->
                        <div class="details-card">
                            <div class="card-header">
                                <span class="card-header-icon">📞</span>
                                <span class="card-title">Liên hệ & Cư trú</span>
                            </div>

                            <div class="info-row">
                                <span class="info-key">Số điện thoại</span>
                                <span class="info-value highlight">${profile.phone}</span>
                            </div>

                            <div class="info-row">
                                <span class="info-key">Email cá nhân</span>
                                <span class="info-value">${profile.email}</span>
                            </div>

                            <div class="info-row">
                                <span class="info-key">Nơi thường trú</span>
                                <span class="info-value">${profile.residence}</span>
                            </div>

                            <div class="info-row">
                                <span class="info-key">Số CCCD / Hộ chiếu</span>
                                <span class="info-value highlight">${profile.citizenId}</span>
                            </div>
                        </div>

                    </div>
                </c:when>

                <%-- Fallback case: Account exists but Profile database record is missing --%>
                <c:otherwise>
                    
                    <!-- ══ FALLBACK HEADER CARD ══ -->
                    <div class="profile-header-card">
                        <div class="profile-banner"></div>
                        <div class="profile-header-content">
                            <div class="profile-large-avatar">
                                ${account.username.substring(0,1)}
                            </div>
                            <div class="profile-header-info">
                                <div class="profile-name-row">
                                    <h1>Tài khoản: ${account.username}</h1>
                                    <span class="profile-status-badge status-empty">
                                        ⚠ Chưa liên kết hồ sơ
                                    </span>
                                </div>
                                <div class="profile-sub-row">
                                    <span>
                                        <strong>Mã tài khoản:</strong> #${account.accountId}
                                    </span>
                                    <span>
                                        <strong>Vai trò hệ thống:</strong> 
                                        <c:choose>
                                            <c:when test="${account.roleId == 3}">Admin Advanced</c:when>
                                            <c:when test="${account.roleId == 2}">Admin</c:when>
                                            <c:otherwise>Common</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Warning Callout Banner -->
                    <div class="empty-profile-callout">
                        <span class="callout-icon">💡</span>
                        <div class="callout-content">
                            <h3>Thông tin hồ sơ chưa sẵn sàng</h3>
                            <p>Tài khoản đăng nhập của bạn hiện tại chưa được liên kết với hồ sơ nhân sự (Citizen Profile) trong hệ thống. Vui lòng liên hệ bộ phận Quản trị viên hệ thống hoặc phòng Nhân sự (HR) để thực hiện cập nhật và liên kết thông tin công dân (Citizen ID: <strong>${account.citizenId != null ? account.citizenId : "Chưa cấu hình"}</strong>) cho tài khoản này.</p>
                        </div>
                    </div>

                    <!-- Basic Account Details Card -->
                    <div class="details-card" style="max-width: 600px;">
                        <div class="card-header">
                            <span class="card-header-icon">🔒</span>
                            <span class="card-title">Thông tin tài khoản bảo mật</span>
                        </div>
                        
                        <div class="info-row">
                            <span class="info-key">Tên đăng nhập</span>
                            <span class="info-value" style="font-weight: 500;">${account.username}</span>
                        </div>
                        
                        <div class="info-row">
                            <span class="info-key">Mã số nhân viên (ID)</span>
                            <span class="info-value highlight">#${account.accountId}</span>
                        </div>

                        <div class="info-row">
                            <span class="info-key">Trạng thái hoạt động</span>
                            <span class="info-value" style="color: var(--success); font-weight: 500;">● Đang hoạt động</span>
                        </div>
                        
                        <div class="info-row">
                            <span class="info-key">Khóa công dân liên kết (Citizen ID)</span>
                            <span class="info-value highlight">${account.citizenId != null ? account.citizenId : "Chưa chỉ định"}</span>
                        </div>
                    </div>

                </c:otherwise>
            </c:choose>

        </div>
    </div>
</div>

</body>
</html>
