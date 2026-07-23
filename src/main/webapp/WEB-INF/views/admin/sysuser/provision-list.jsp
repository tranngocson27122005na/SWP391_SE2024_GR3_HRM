<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Cấp TK nhân viên &middot; HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/admin-table.css">
</head>
<body class="app-shell">
<jsp:include page="/WEB-INF/fragment/top-nav.jsp"/>
<main class="app-main app-main--solo">
    <h1 class="page-title">Emp chưa có tài khoản</h1>
    <p class="page-subtitle">BR-ADM-SYNC-01 — kiểm tra mỗi ngày làm việc (ADR-0004). Username = mã NV, MK mặc định password.</p>
    <c:if test="${not empty flashError}"><p class="flash-error">${flashError}</p></c:if>
    <div class="panel">
        <table class="data-table">
            <thead><tr><th>Mã</th><th>Họ tên</th><th>Chức vụ</th><th>Phòng</th><th></th></tr></thead>
            <tbody>
            <c:forEach var="e" items="${employees}">
                <tr>
                    <td>${e.employeeCode}</td>
                    <td>${e.fullName}</td>
                    <td>${e.positionName}</td>
                    <td>${e.departmentName}</td>
                    <td>
                        <form method="post" action="${pageContext.request.contextPath}/sys-user/provision">
                            <input type="hidden" name="employeeId" value="${e.employeeId}">
                            <button class="btn" type="submit">Cấp TK</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty employees}"><tr><td colspan="5">Không còn emp thiếu TK.</td></tr></c:if>
            </tbody>
        </table>
        <p><a class="btn btn-secondary" href="${pageContext.request.contextPath}/sys-user/list">Danh sách tài khoản</a></p>
    </div>
</main>
</body>
</html>
