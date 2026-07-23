<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tham số lương &middot; HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/admin-table.css">
</head>
<body class="app-shell">
<jsp:include page="/WEB-INF/fragment/top-nav.jsp"/>
<main class="app-main app-main--solo">
    <h1 class="page-title">Tham số tính lương</h1>
    <p class="page-subtitle">Giá trị hiện hành — snapshot khi chạy payroll</p>
    <c:if test="${param.ok == '1'}"><p class="flash-ok">Đã lưu tham số.</p></c:if>
    <c:if test="${not empty errorMessage}"><p class="flash-error">${errorMessage}</p></c:if>
    <div class="panel">
        <form method="post" action="${pageContext.request.contextPath}/payslip/update">
            <table class="data-table">
                <thead>
                <tr>
                    <th>Mã</th>
                    <th>Giá trị</th>
                    <th>Ghi chú</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="p" items="${params}">
                    <tr>
                        <td>${p.paramCode}</td>
                        <td>
                            <input name="param_${p.paramCode}" value="${p.paramValue}" required>
                        </td>
                        <td>${p.note}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty params}">
                    <tr><td colspan="3">Chưa có param — chạy sql/1.attendance_payroll_mvp.sql.</td></tr>
                </c:if>
                </tbody>
            </table>
            <p>
                <button class="btn" type="submit">Lưu</button>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/payslip/list">Danh sách phiếu</a>
            </p>
        </form>
    </div>
</main>
</body>
</html>
