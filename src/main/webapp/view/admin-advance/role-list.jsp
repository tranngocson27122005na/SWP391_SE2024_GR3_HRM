<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 5/20/2026
  Time: 5:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>role list</title>
</head>
<body>
<form action="role-list" method="post">
    <h2>Danh sách Role</h2>
    <table border="1">
        <tr>
            <th>ID</th>
            <th>Tên Role</th>
            <th>Active</th>
        </tr>
        <c:forEach var="role" items="${roles}">
            <tr>
                <td>${role.roleId}</td>
                <td>${role.roleName}</td>
                <td>${role.isActive}</td>
            </tr>
        </c:forEach>
    </table>
</form>
</body>
</html>
