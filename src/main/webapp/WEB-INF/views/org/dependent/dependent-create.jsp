<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thêm NPT &middot; HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/admin-table.css">
</head>
<body class="app-shell">
<jsp:include page="/WEB-INF/fragment/top-nav.jsp"/>
<main class="app-main app-main--solo">
    <h1 class="page-title">Thêm người phụ thuộc</h1>
    <c:if test="${not empty errorMessage}"><p class="flash-error">${errorMessage}</p></c:if>
    <div class="panel">
        <form method="post" action="${pageContext.request.contextPath}/dependent/create">
            <input type="hidden" name="employeeId" value="${employeeId}">
            <label>Họ tên <input name="fullName" required value="${form.fullName}"></label>
            <label>Quan hệ <input name="relationship" required value="${form.relationship}"></label>
            <label>MST <input name="taxCode" value="${form.taxCode}"></label>
            <label>Từ ngày <input type="date" name="startDate" value="${form.startDate}"></label>
            <label>Đến ngày <input type="date" name="endDate" value="${form.endDate}"></label>
            <p>
                <button class="btn" type="submit">Lưu</button>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/dependent/list?employeeId=${employeeId}">Hủy</a>
            </p>
        </form>
    </div>
</main>
</body>
</html>
