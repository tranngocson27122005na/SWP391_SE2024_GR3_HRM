<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Import chấm công &middot; HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/admin-table.css">
</head>
<body class="app-shell">
<jsp:include page="/WEB-INF/fragment/top-nav.jsp"/>
<main class="app-main app-main--solo">
    <h1 class="page-title">Import chấm công (CSV)</h1>
    <p class="page-subtitle">Import kỳ mới sẽ xóa kết quả payroll nháp hiện có</p>
    <c:if test="${not empty errorMessage}"><p class="flash-error">${errorMessage}</p></c:if>
    <c:if test="${not empty successMessage}"><p class="flash-ok">${successMessage}</p></c:if>
    <div class="panel">
        <form method="post" enctype="multipart/form-data"
              action="${pageContext.request.contextPath}/attendance/import">
            <label>Tháng
                <input type="number" name="periodMonth" min="1" max="12" required value="${periodMonth}">
            </label>
            <label>Năm
                <input type="number" name="periodYear" min="2000" required value="${periodYear}">
            </label>
            <label>File CSV
                <input type="file" name="csvFile" accept=".csv,text/csv" required>
            </label>
            <p>
                <button class="btn" type="submit">Import</button>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/attendance/list">Xem summary</a>
            </p>
        </form>
        <c:if test="${not empty importErrors}">
            <h2 class="page-subtitle">Cảnh báo / lỗi dòng</h2>
            <ul>
                <c:forEach var="err" items="${importErrors}">
                    <li>${err}</li>
                </c:forEach>
            </ul>
        </c:if>
    </div>
</main>
</body>
</html>
