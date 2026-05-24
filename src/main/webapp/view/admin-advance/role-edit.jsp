<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Sửa vai trò</title>
</head>
<body>
<jsp:include page="/fragment/top-nav.jsp" />

<h2>Cập nhật thông tin vai trò</h2>

<c:if test="${not empty error}">
    <p>${error}</p>
</c:if>

<form action="${pageContext.request.contextPath}/admin-advance/role-edit" method="post">
    <input type="hidden" name="roleId" value="${role.roleId}" />
    <table>
        <tr>
            <td>Mã vai trò:</td>
            <td>${role.roleId}</td>
        </tr>
        <tr>
            <td>Tên vai trò:</td>
            <td><input type="text" name="roleName" value="${role.roleName}" required /></td>
        </tr>
        <tr>
            <td>Mô tả:</td>
            <td><input type="text" name="roleDescription" value="${role.roleDescription}" /></td>
        </tr>
        <tr>
            <td>Trạng thái:</td>
            <td>
                <c:choose>
                    <c:when test="${role.isActive}">Đang hoạt động</c:when>
                    <c:otherwise>Không hoạt động</c:otherwise>
                </c:choose>
                (đổi trạng thái tại danh sách vai trò)
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <button type="submit">Lưu</button>
                <a href="${pageContext.request.contextPath}/admin-advance/role-list">Hủy</a>
            </td>
        </tr>
    </table>
</form>
</body>
</html>
