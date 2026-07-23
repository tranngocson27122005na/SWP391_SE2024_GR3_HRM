<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tài khoản hệ thống &middot; HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/admin-table.css">
</head>
<body class="app-shell">
<jsp:include page="/WEB-INF/fragment/top-nav.jsp"/>
<main class="app-main app-main--solo">
    <h1 class="page-title">Tài khoản hệ thống</h1>
    <p class="page-subtitle">Khóa/mở và đổi mật khẩu giúp người dùng</p>

    <c:if test="${not empty sessionScope.flashError}">
        <div class="flash flash--error">${sessionScope.flashError}</div>
        <c:remove var="flashError" scope="session"/>
    </c:if>

    <div class="panel">
        <form class="toolbar" method="get" action="${pageContext.request.contextPath}/sys-user/list">
            <input type="text" name="keyword" value="${keyword}" placeholder="Username / nhân viên...">
            <select name="status">
                <option value="">Tất cả trạng thái</option>
                <option value="1" ${statusFilter == 1 ? 'selected' : ''}>Active</option>
                <option value="0" ${statusFilter == 0 ? 'selected' : ''}>Inactive</option>
            </select>
            <button class="btn btn-secondary" type="submit">Lọc</button>
        </form>

        <table class="data-table">
            <thead>
            <tr>
                <th>Username</th>
                <th>Vai trò</th>
                <th>Nhân viên</th>
                <th>Trạng thái</th>
                <th>Ngày tạo</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="u" items="${users}">
                <tr>
                    <td>${u.username}</td>
                    <td>${u.roleName}</td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty u.employeeCode}">
                                ${u.employeeCode} — ${u.employeeName}
                            </c:when>
                            <c:otherwise>—</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${u.status == 1}"><span class="badge-ok">Active</span></c:when>
                            <c:otherwise><span class="badge-off">Inactive</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td><fmt:formatDate value="${u.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                    <td class="actions">
                        <form method="post" action="${pageContext.request.contextPath}/sys-user/update-status" style="display:inline">
                            <input type="hidden" name="userId" value="${u.userId}">
                            <c:choose>
                                <c:when test="${u.status == 1}">
                                    <input type="hidden" name="status" value="0">
                                    <button class="btn btn-danger" type="submit"
                                            onclick="return confirm('Khóa tài khoản ${u.username}?');">Khóa</button>
                                </c:when>
                                <c:otherwise>
                                    <input type="hidden" name="status" value="1">
                                    <button class="btn btn-secondary" type="submit">Mở</button>
                                </c:otherwise>
                            </c:choose>
                        </form>
                        <a class="btn btn-secondary"
                           href="${pageContext.request.contextPath}/sys-user/reset-password?userId=${u.userId}&amp;username=${u.username}">
                            Đổi MK
                        </a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty users}">
                <tr><td colspan="6">Không có tài khoản.</td></tr>
            </c:if>
            </tbody>
        </table>

        <jsp:include page="/WEB-INF/fragment/paging.jsp"/>
    </div>
</main>
</body>
</html>
