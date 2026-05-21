<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập – HRM System</title>
</head>
<body>

<h2>Đăng nhập hệ thống HRM</h2>

<!-- Hiển thị lỗi nếu có -->
<c:if test="${not empty error}">
    <p style="color:red;">${error}</p>
</c:if>

<form action="${pageContext.request.contextPath}/login" method="post">
    <label for="username">Tên đăng nhập:</label>
    <input type="text" id="username" name="username" required /><br><br>

    <label for="password">Mật khẩu:</label>
    <input type="password" id="password" name="password" required /><br><br>

    <button type="submit">Đăng nhập</button>
</form>

<p>
    <a href="${pageContext.request.contextPath}/forgot-password">Quên mật khẩu?</a>
</p>

<hr>

</body>
</html>
