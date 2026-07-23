<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết phiếu lương &middot; HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/admin-table.css">
</head>
<body class="app-shell">
<jsp:include page="/WEB-INF/fragment/top-nav.jsp"/>
<main class="app-main app-main--solo">
    <h1 class="page-title">Phiếu lương — ${payslip.employeeCode}</h1>
    <p class="page-subtitle">${payslip.fullName} · Net
        <fmt:formatNumber value="${payslip.netPay}" type="number" maxFractionDigits="0"/>
    </p>
    <div class="panel">
        <table class="data-table">
            <thead>
            <tr>
                <th>Mã element</th>
                <th>Tên</th>
                <th>Số tiền</th>
                <th>Ghi chú</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="d" items="${details}">
                <tr>
                    <td>${d.elementCode}</td>
                    <td>${d.elementName}</td>
                    <td><fmt:formatNumber value="${d.amount}" type="number" maxFractionDigits="0"/></td>
                    <td>${d.remark}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <p>
            <a class="btn btn-secondary"
               href="${pageContext.request.contextPath}/payslip/list?batchId=${payslip.batchId}">Quay lại</a>
        </p>
    </div>
</main>
</body>
</html>
