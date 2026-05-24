<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Trang chủ HRM</title>
</head>
<body>
<jsp:include page="/fragment/top-nav.jsp" />

<h2>Xin chào, ${sessionScope.user.username}</h2>
<p>Chào mừng bạn đến với hệ thống HRM.</p>

<ul>
    <li><a href="${pageContext.request.contextPath}/profile">Xem hồ sơ cá nhân</a></li>
    <li><a href="${pageContext.request.contextPath}/edit-profile">Chỉnh sửa hồ sơ</a></li>
    <li><a href="${pageContext.request.contextPath}/change-password">Đổi mật khẩu</a></li>
    <c:if test="${sessionScope.account.roleId == 2 || sessionScope.account.roleId == 3}">
        <li><a href="${pageContext.request.contextPath}/admin/user-list">Quản lý tài khoản</a></li>
    </c:if>
    <c:if test="${sessionScope.account.roleId == 3}">
        <li><a href="${pageContext.request.contextPath}/admin-advance/role-list">Quản lý vai trò &amp; phân quyền</a></li>
    </c:if>
</ul>
</body>
</html>
