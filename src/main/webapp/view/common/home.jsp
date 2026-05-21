<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Trang chủ – MyWebApp</title>
</head>
<body>
<jsp:include page="/fragment/top-nav.jsp" />

<h2>Xin chào, ${sessionScope.user.username}</h2>
<p>Chào mừng bạn đến với trang chủ của hệ thống HRM/MyWebApp.</p>

<ul>
    <li>Xem hồ sơ cá nhân</li>
    <li>Quản lý tài khoản</li>
    <li>Thông tin liên hệ</li>
</ul>

<p><a href="${pageContext.request.contextPath}/logout">Đăng xuất</a></p>

</body>
</html>
