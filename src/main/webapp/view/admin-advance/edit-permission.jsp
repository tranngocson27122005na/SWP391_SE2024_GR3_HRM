<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Edit Permission</title>
</head>
<body>
<h2>Chỉnh sửa quyền hạn</h2>

<form action="${pageContext.request.contextPath}/edit-permission" method="post">
    <input type="hidden" name="permissionId" value="${permission.permissionId}" />

    <p>
        <label for="permissionName">Tên quyền hạn:</label><br/>
        <input type="text" id="permissionName" name="permissionName"
               value="${permission.permissionName}" />
    </p>

    <p>
        <label for="description">Mô tả chức năng:</label><br/>
        <textarea id="description" name="description" rows="4" cols="40">${permission.description}</textarea>
    </p>

    <p>
        <label for="roleId">Áp dụng cho vai trò:</label><br/>
        <select id="roleId" name="roleId">
            <c:forEach var="role" items="${roles}">
                <option value="${role.roleId}"
                        <c:if test="${role.roleId == permission.roleId}">selected</c:if>>
                        ${role.roleName}
                </option>
            </c:forEach>
        </select>
    </p>

    <p>
        <button type="submit">Lưu thay đổi</button>
        <a href="${pageContext.request.contextPath}/permission-list">Hủy</a>
    </p>
</form>
</body>
</html>
