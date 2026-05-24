<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Phân quyền vai trò</title>
</head>
<body>
<jsp:include page="/fragment/top-nav.jsp" />

<h2>Phân quyền: ${role.roleName}</h2>

<c:if test="${param.success == 'updated'}">
    <p>Cập nhật phân quyền thành công.</p>
</c:if>

<form action="${pageContext.request.contextPath}/admin-advance/role-permissions" method="post">
    <input type="hidden" name="roleId" value="${role.roleId}" />
    <table border="1" cellpadding="4">
        <thead>
        <tr>
            <th>Chọn</th>
            <th>ID</th>
            <th>Tên quyền</th>
            <th>Mô tả</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="perm" items="${permissions}">
            <tr>
                <td>
                    <input type="checkbox" name="permissionIds" value="${perm.permissionId}"
                           <c:if test="${assignedPermissionIds.contains(perm.permissionId)}">checked</c:if> />
                </td>
                <td>${perm.permissionId}</td>
                <td>${perm.permissionName}</td>
                <td>${perm.permissionDescription}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <p>
        <button type="submit">Lưu phân quyền</button>
        <a href="${pageContext.request.contextPath}/admin-advance/role-list">Quay lại danh sách vai trò</a>
    </p>
</form>
</body>
</html>
