<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Hồ sơ cá nhân</title>
</head>
<body>
<jsp:include page="/fragment/top-nav.jsp" />

<h2>Hồ sơ cá nhân</h2>

<c:if test="${not empty message}">
    <p>${message}</p>
</c:if>
<c:if test="${not empty error}">
    <p>${error}</p>
</c:if>

<c:choose>
    <c:when test="${not empty profile}">
        <table>
            <tr><td>Tên đăng nhập:</td><td>${account.username}</td></tr>
            <tr><td>Mã công dân:</td><td>${profile.citizenId}</td></tr>
            <tr><td>Họ tên:</td><td>${profile.fullName}</td></tr>
            <tr><td>Email:</td><td>${profile.email}</td></tr>
            <tr><td>Điện thoại:</td><td>${profile.phone}</td></tr>
            <tr><td>Địa chỉ:</td><td>${profile.address}</td></tr>
            <tr><td>Ngày sinh:</td>
                <td><fmt:formatDate value="${profile.dateOfBirth}" pattern="yyyy-MM-dd"/></td></tr>
            <tr><td>Giới tính:</td>
                <td>
                    <c:choose>
                        <c:when test="${profile.gender}">Nam</c:when>
                        <c:when test="${profile.gender == false}">Nữ</c:when>
                        <c:otherwise>—</c:otherwise>
                    </c:choose>
                </td></tr>
            <tr><td>Hôn nhân:</td>
                <td>
                    <c:choose>
                        <c:when test="${profile.married}">Đã kết hôn</c:when>
                        <c:when test="${profile.married == false}">Độc thân</c:when>
                        <c:otherwise>—</c:otherwise>
                    </c:choose>
                </td></tr>
        </table>
        <p>
            <a href="${pageContext.request.contextPath}/edit-profile">Chỉnh sửa hồ sơ</a> |
            <a href="${pageContext.request.contextPath}/change-password">Đổi mật khẩu</a> |
            <a href="${pageContext.request.contextPath}/common/home">Trang chủ</a>
        </p>
    </c:when>
    <c:otherwise>
        <p>Chưa có thông tin hồ sơ.</p>
    </c:otherwise>
</c:choose>
</body>
</html>
