<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Danh sách Tài khoản</title>
</head>
<body>
<jsp:include page="/fragment/top-nav.jsp" />
<h2>Danh sách tài khoản hệ thống</h2>

<c:if test="${not empty message}">
    <p>${message}</p>
</c:if>
<c:if test="${not empty error}">
    <p>${error}</p>
</c:if>

<p>
    <a href="${pageContext.request.contextPath}/admin/user?action=detail&id=0">+ Thêm tài khoản mới</a>
</p>

<form action="${pageContext.request.contextPath}/admin/user-list" method="get">
    <label>Tìm kiếm:</label>
    <input type="text" name="keyword" value="${keyword}" />
    <button type="submit">Tìm</button>
</form>

<table border="1" cellpadding="4">
    <thead>
    <tr>
        <th>ID</th>
        <th>Username</th>
        <th>Họ tên</th>
        <th>Email</th>
        <th>Role</th>
        <th>Citizen ID</th>
        <th>Trạng thái</th>
        <th>Hành động</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${userList}" var="user">
        <tr>
            <td>${user.accountId}</td>
            <td>${user.username}</td>
            <td>${user.fullName}</td>
            <td>${user.email}</td>
            <td>${user.roleName} (${user.roleId})</td>
            <td>${user.citizenId != null ? user.citizenId : "Chưa liên kết"}</td>
            <td>
                <c:choose>
                    <c:when test="${user.isActive}">Đang hoạt động</c:when>
                    <c:otherwise>Bị khóa</c:otherwise>
                </c:choose>
            </td>
            <td>
                <form action="${pageContext.request.contextPath}/admin/user" method="post">
                    <input type="hidden" name="accountId" value="${user.accountId}" />
                    <input type="hidden" name="keyword" value="${keyword}" />
                    <input type="hidden" name="page" value="${pagging.displayIndex}" />
                    <label>
                        <input type="radio" name="isActive" value="true" <c:if test="${user.isActive}">checked</c:if> />
                        Hoạt động
                    </label>
                    <label>
                        <input type="radio" name="isActive" value="false" <c:if test="${!user.isActive}">checked</c:if> />
                        Khóa
                    </label>
                    <button type="submit">Cập nhật</button>
                </form>
                <a href="${pageContext.request.contextPath}/admin/user?action=detail&id=${user.accountId}">Chi tiết</a>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty userList}">
        <tr><td colspan="8">Không có dữ liệu.</td></tr>
    </c:if>
    </tbody>
</table>

<c:if test="${not empty pagging}">
    <p>
        Trang ${pagging.displayIndex} / ${pagging.totalPage} (tổng ${pagging.size} bản ghi)
    </p>
    <c:if test="${pagging.index > 0}">
        <a href="${pageContext.request.contextPath}/admin/user-list?page=${pagging.displayIndex - 1}&keyword=${keyword}">Trước</a>
    </c:if>
    <c:if test="${pagging.index + 1 < pagging.totalPage}">
        <a href="${pageContext.request.contextPath}/admin/user-list?page=${pagging.displayIndex + 1}&keyword=${keyword}">Sau</a>
    </c:if>
</c:if>
</body>
</html>
