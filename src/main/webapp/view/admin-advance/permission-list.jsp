<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 5/20/2026
  Time: 7:45 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>permission list</title>
</head>
<body>
<form action="permission-list" method="post">
    <h2>Danh sách Permission</h2>
    <table border="1">
        <tr>
            <th>ID</th>
            <th>Tên Permission</th>
            <th>Mô tả</th>
            <th>Role ID</th>
        </tr>
        <c:forEach var="permission" items="${permissions}">
            <tr>
                <td>${permission.permissionId}</td>
                <td>${permission.permissionName}</td>
                <td>${permission.description}</td>
                <td>${permission.roleId}</td>
            </tr>
        </c:forEach>
    </table>
</form>

</body>
</html>
