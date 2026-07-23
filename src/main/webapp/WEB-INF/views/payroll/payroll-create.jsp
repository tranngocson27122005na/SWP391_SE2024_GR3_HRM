<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chạy tính lương &middot; HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/admin-table.css">
</head>
<body class="app-shell">
<jsp:include page="/WEB-INF/fragment/top-nav.jsp"/>
<main class="app-main app-main--solo">
    <h1 class="page-title">Chạy tính lương (nháp)</h1>
    <p class="page-subtitle">Xóa batch cũ, tạo batch DRAFT mới theo kỳ đã import</p>
    <c:if test="${not empty errorMessage}"><p class="flash-error">${errorMessage}</p></c:if>
    <div class="panel">
        <form method="post" action="${pageContext.request.contextPath}/payslip/create">
            <label>Tháng
                <input type="number" name="periodMonth" min="1" max="12" required value="${periodMonth}">
            </label>
            <label>Năm
                <input type="number" name="periodYear" min="2000" required value="${periodYear}">
            </label>
            <p>
                <button class="btn" type="submit">Tính lương</button>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/payslip/list">Danh sách</a>
            </p>
        </form>
    </div>
</main>
</body>
</html>
