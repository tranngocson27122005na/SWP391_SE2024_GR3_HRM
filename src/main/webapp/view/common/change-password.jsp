<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đổi mật khẩu</title>
</head>
<body>
<jsp:include page="/fragment/top-nav.jsp" />

<h2>Đổi mật khẩu</h2>

<c:if test="${not empty message}">
    <p>${message}</p>
</c:if>
<c:if test="${not empty error}">
    <p>${error}</p>
</c:if>

<form action="${pageContext.request.contextPath}/change-password" method="post">
    <table>
        <tr>
            <td>Mật khẩu hiện tại:</td>
            <td><input type="password" name="oldPassword" required /></td>
        </tr>
        <tr>
            <td>Mật khẩu mới:</td>
            <td><input type="password" name="newPassword" required /></td>
        </tr>
        <tr>
            <td>Xác nhận mật khẩu mới:</td>
            <td><input type="password" name="confirmPassword" required /></td>
        </tr>
        <tr>
            <td colspan="2">
                <button type="submit">Cập nhật</button>
                <a href="${pageContext.request.contextPath}/profile">Quay lại hồ sơ</a>
            </td>
        </tr>
    </table>
</form>
</body>
</html>
