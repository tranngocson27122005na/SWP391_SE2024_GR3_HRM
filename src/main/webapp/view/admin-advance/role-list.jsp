<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách Vai trò</title>
</head>
<body>
<jsp:include page="/fragment/top-nav.jsp" />
<h2>Danh sách Vai trò</h2>

<c:if test="${not empty message}">
    <p>${message}</p>
</c:if>
<c:if test="${not empty error}">
    <p>${error}</p>
</c:if>

<table border="1" cellpadding="4">
    <thead>
    <tr>
        <th>Mã ID</th>
        <th>Tên vai trò</th>
        <th>Mô tả</th>
        <th>Trạng thái</th>
        <th>Hành động</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="role" items="${roles}">
        <tr>
            <td>${role.roleId}</td>
            <td>${role.roleName}</td>
            <td>${role.roleDescription}</td>
            <td colspan="1">
                <form action="${pageContext.request.contextPath}/admin-advance/role-list" method="post">
                    <input type="hidden" name="roleId" value="${role.roleId}" />
                    <label>
                        <input type="radio" name="isActive" value="true" <c:if test="${role.isActive}">checked</c:if> />
                        Hoạt động
                    </label>
                    <label>
                        <input type="radio" name="isActive" value="false" <c:if test="${!role.isActive}">checked</c:if> />
                        Không hoạt động
                    </label>
                    <button type="submit">Cập nhật</button>
                </form>
            </td>
            <td>
                <a href="${pageContext.request.contextPath}/admin-advance/role-edit?roleId=${role.roleId}">Sửa thông tin</a> |
                <a href="${pageContext.request.contextPath}/admin-advance/role-permissions?roleId=${role.roleId}">Phân quyền</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<p>
    <a href="${pageContext.request.contextPath}/admin/user-list">Quản lý tài khoản</a> |
    <a href="${pageContext.request.contextPath}/common/home">Trang chủ</a>
</p>
</body>
</html>
