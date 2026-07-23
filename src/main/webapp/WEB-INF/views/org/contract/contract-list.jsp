<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hợp đồng &middot; HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/admin-table.css">
</head>
<body class="app-shell">
<jsp:include page="/WEB-INF/fragment/top-nav.jsp"/>
<main class="app-main app-main--solo">
    <h1 class="page-title">Hợp đồng nhân viên #${employeeId}</h1>
    <p>
        <c:if test="${canCreate}">
            <a class="btn" href="${pageContext.request.contextPath}/contract/create?employeeId=${employeeId}">Thêm / chuyển HĐ</a>
        </c:if>
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/employee/detail?id=${employeeId}">Hồ sơ</a>
    </p>
    <div class="panel">
        <table class="data-table">
            <thead>
            <tr><th>Loại</th><th>Bắt đầu</th><th>Kết thúc</th><th>Lương</th><th>Hình thức</th><th>Trạng thái</th><th></th></tr>
            </thead>
            <tbody>
            <c:forEach var="c" items="${contracts}">
                <tr>
                    <td>${c.contractTypeLabel}<c:if test="${c.current}"> *</c:if></td>
                    <td><fmt:formatDate value="${c.startDate}" pattern="dd/MM/yyyy"/></td>
                    <td><fmt:formatDate value="${c.endDate}" pattern="dd/MM/yyyy"/></td>
                    <td><fmt:formatNumber value="${c.basicSalary}" type="number"/></td>
                    <td>${c.salaryTypeLabel}</td>
                    <td>${c.status == 1 ? 'ACTIVE' : 'INACTIVE'}</td>
                    <td>
                        <c:if test="${canUpdate and c.status != 1}">
                            <form style="display:inline" method="post" action="${pageContext.request.contextPath}/contract/update">
                                <input type="hidden" name="id" value="${c.contractId}">
                                <input type="hidden" name="employeeId" value="${employeeId}">
                                <input type="hidden" name="action" value="activate">
                                <button class="btn btn-secondary" type="submit">Activate</button>
                            </form>
                        </c:if>
                        <c:if test="${canDelete and c.status == 1}">
                            <form style="display:inline" method="post" action="${pageContext.request.contextPath}/contract/delete">
                                <input type="hidden" name="id" value="${c.contractId}">
                                <input type="hidden" name="employeeId" value="${employeeId}">
                                <button class="btn btn-secondary" type="submit">INACTIVE</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty contracts}">
                <tr><td colspan="7">Chưa có hợp đồng.</td></tr>
            </c:if>
            </tbody>
        </table>
        <c:if test="${empty contracts and canCreate}">
            <p class="page-subtitle" style="margin-top:12px">
                Chưa có HĐ — tạo HĐ đầu để gắn làm hiện hành.
                <a class="btn" href="${pageContext.request.contextPath}/contract/create?employeeId=${employeeId}">Tạo hợp đồng đầu</a>
            </p>
        </c:if>
        <p class="page-subtitle">* = HĐ hiện hành</p>
    </div>
</main>
</body>
</html>
