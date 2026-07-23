<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Phiếu lương &middot; HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/admin-table.css">
</head>
<body class="app-shell">
<jsp:include page="/WEB-INF/fragment/top-nav.jsp"/>
<main class="app-main app-main--solo">
    <h1 class="page-title">Danh sách phiếu lương</h1>
    <c:if test="${not empty createdCount}">
        <p class="flash-ok">Đã tạo ${createdCount} phiếu lương.</p>
    </c:if>
    <c:if test="${not empty errorMessage}"><p class="flash-error">${errorMessage}</p></c:if>
    <div class="panel">
        <form class="toolbar" method="get" action="${pageContext.request.contextPath}/payslip/list">
            <label>Batch
                <select name="batchId" onchange="this.form.submit()">
                    <c:forEach var="b" items="${batches}">
                        <option value="${b.batchId}" ${b.batchId == batchId ? 'selected' : ''}>
                            #${b.batchId} — ${b.batchName} (${b.periodMonth}/${b.periodYear})
                        </option>
                    </c:forEach>
                </select>
            </label>
            <a class="btn" href="${pageContext.request.contextPath}/payslip/create">Chạy tính lương</a>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/payslip/edit">Tham số</a>
        </form>
        <c:if test="${not empty currentBatch}">
            <p class="page-subtitle">Tổng net: ${currentBatch.totalNet}</p>
        </c:if>
        <table class="data-table">
            <thead>
            <tr>
                <th>Mã NV</th>
                <th>Họ tên</th>
                <th>Net pay</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="p" items="${payslips}">
                <tr>
                    <td>${p.employeeCode}</td>
                    <td>${p.fullName}</td>
                    <td>${p.netPay}</td>
                    <td>
                        <a class="btn btn-secondary"
                           href="${pageContext.request.contextPath}/payslip/detail?id=${p.payslipId}">Chi tiết</a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty payslips}">
                <tr><td colspan="4">Chưa có phiếu — chạy tính lương trước.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</main>
</body>
</html>
