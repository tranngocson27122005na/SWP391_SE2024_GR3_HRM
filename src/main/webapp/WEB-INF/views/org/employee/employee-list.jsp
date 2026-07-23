<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nhân viên &middot; HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/admin-table.css">
</head>
<body class="app-shell">
<jsp:include page="/WEB-INF/fragment/top-nav.jsp"/>
<main class="app-main app-main--solo">
    <h1 class="page-title">Nhân viên</h1>
    <p class="page-subtitle">Đang làm + đã nghỉ (theo HĐ hiện hành)</p>
    <c:if test="${canCreate}">
        <p><a class="btn" href="${pageContext.request.contextPath}/employee/create">Thêm nhân viên</a></p>
    </c:if>

    <div class="panel">
        <form class="toolbar" method="get" action="${pageContext.request.contextPath}/employee/list">
            <input type="text" name="keyword" value="${keyword}" placeholder="Mã hoặc họ tên...">
            <button class="btn btn-secondary" type="submit">Tìm</button>
        </form>

        <table class="data-table">
            <thead>
            <tr>
                <th>Mã</th>
                <th>Họ tên</th>
                <th>Chức vụ</th>
                <th>Phòng ban</th>
                <th>Trạng thái LĐ</th>
                <th>Ngày vào</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="item" items="${employees}">
                <tr>
                    <td>${item.employeeCode}</td>
                    <td>${item.fullName}</td>
                    <td>${item.positionName}</td>
                    <td>${item.departmentName}</td>
                    <td>${item.working ? 'Đang làm' : 'Chưa có HĐ / Đã nghỉ'}</td>
                    <td><fmt:formatDate value="${item.joiningDate}" pattern="dd/MM/yyyy"/></td>
                    <td>
                        <a class="btn btn-secondary"
                           href="${pageContext.request.contextPath}/employee/detail?id=${item.employeeId}">Chi tiết</a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty employees}">
                <tr><td colspan="7">Không có dữ liệu.</td></tr>
            </c:if>
            </tbody>
        </table>

        <jsp:include page="/WEB-INF/fragment/paging.jsp"/>
    </div>
</main>
</body>
</html>
