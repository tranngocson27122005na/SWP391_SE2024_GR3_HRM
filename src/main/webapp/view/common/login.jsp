<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 5/20/2026
  Time: 5:57 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/login" method="post">
    <label for="username">Tên đăng nhập:</label>
    <input type="text" id="username" name="username" required /><br/>

    <label for="password">Mật khẩu:</label>
    <input type="password" id="password" name="password" required /><br/>

    <button type="submit">Login</button>
</form>

<c:if test="${not empty error}">
    <p style="color:red">${error}</p>
</c:if>
</body>
</html>
