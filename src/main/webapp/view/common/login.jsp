<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập – HRM System</title>
</head>
<body>

<h2>Đăng nhập hệ thống HRM</h2>

<!-- Hiển thị lỗi nếu có -->
<%--<c:if test="${not empty error}">--%>
<%--    <p style="color:red;">${error}</p>--%>
<%--</c:if>--%>

<form action="${pageContext.request.contextPath}/login" method="post">
    <table>
        <tr>
            <td>Tên đăng nhập:</td>
            <td><input type="text" id="username" name="username" required autocomplete="off"/></td>
        </tr>
        <tr>
            <td>Mật khẩu:</td>
            <td><input type="password" id="password" name="password" required autocomplete="new-password"/></td>
        </tr>
        <tr>
            <td colspan="2"><button type="submit">Đăng nhập</button></td>
        </tr>
        <tr>
            <td><a href="${pageContext.request.contextPath}/forgot-password">Quên mật khẩu?</a></td>
        </tr>
    </table>
</form>
</body>
</html>
