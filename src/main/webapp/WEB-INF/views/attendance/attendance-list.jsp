<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Summary chấm công &middot; HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/admin-table.css">
</head>
<body class="app-shell">
<jsp:include page="/WEB-INF/fragment/top-nav.jsp"/>
<main class="app-main app-main--solo">
    <h1 class="page-title">Attendance summary</h1>
    <c:if test="${not empty errorMessage}"><p class="flash-error">${errorMessage}</p></c:if>
    <div class="panel">
        <form class="toolbar" method="get" action="${pageContext.request.contextPath}/attendance/list">
            <label>Tháng <input type="number" name="periodMonth" min="1" max="12" value="${periodMonth}"></label>
            <label>Năm <input type="number" name="periodYear" min="2000" value="${periodYear}"></label>
            <button class="btn btn-secondary" type="submit">Lọc</button>
            <a class="btn" href="${pageContext.request.contextPath}/attendance/import">Import CSV</a>
        </form>
        <table class="data-table">
            <thead>
            <tr>
                <th>Mã NV</th>
                <th>Họ tên</th>
                <th>Ngày phải làm</th>
                <th>Ngày thực tế</th>
                <th>OT WD</th>
                <th>OT WE</th>
                <th>OT HOL</th>
                <th>Muộn/sớm</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="r" items="${rows}">
                <tr>
                    <td>${r.employeeCode}</td>
                    <td>${r.fullName}</td>
                    <td><fmt:formatNumber value="${r.totalWorkDays}" maxFractionDigits="2"/></td>
                    <td><fmt:formatNumber value="${r.actualWorkDays}" maxFractionDigits="2"/></td>
                    <td><fmt:formatNumber value="${r.otWeekdayHours}" maxFractionDigits="2"/></td>
                    <td><fmt:formatNumber value="${r.otWeekendHours}" maxFractionDigits="2"/></td>
                    <td><fmt:formatNumber value="${r.otHolidayHours}" maxFractionDigits="2"/></td>
                    <td>${r.lateEarlyBlocks}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty rows}">
                <tr><td colspan="8">Không có dữ liệu kỳ này.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</main>
</body>
</html>
