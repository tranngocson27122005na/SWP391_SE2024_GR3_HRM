<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Người phụ thuộc &middot; HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/admin-table.css">
</head>
<body class="app-shell">
<jsp:include page="/WEB-INF/fragment/top-nav.jsp"/>
<main class="app-main app-main--solo">
    <h1 class="page-title">Người phụ thuộc — NV #${employeeId}</h1>
    <p>
        <c:if test="${canCreate}">
            <a class="btn" href="${pageContext.request.contextPath}/dependent/create?employeeId=${employeeId}">Thêm NPT</a>
        </c:if>
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/employee/detail?id=${employeeId}">Hồ sơ</a>
    </p>
    <div class="panel">
        <table class="data-table">
            <thead><tr><th>Họ tên</th><th>Quan hệ</th><th>MST</th><th>Trạng thái</th><th></th></tr></thead>
            <tbody>
            <c:forEach var="d" items="${dependents}">
                <tr>
                    <td>${d.fullName}</td>
                    <td>${d.relationship}</td>
                    <td>${empty d.taxCode ? '—' : d.taxCode}</td>
                    <td>${d.status == 1 ? 'ACTIVE' : 'INACTIVE'}</td>
                    <td>
                        <c:if test="${canDelete and d.status == 1}">
                            <form method="post" action="${pageContext.request.contextPath}/dependent/delete">
                                <input type="hidden" name="id" value="${d.dependentId}">
                                <input type="hidden" name="employeeId" value="${employeeId}">
                                <button class="btn btn-secondary" type="submit">INACTIVE</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty dependents}"><tr><td colspan="5">Chưa có NPT.</td></tr></c:if>
            </tbody>
        </table>
    </div>
</main>
</body>
</html>
