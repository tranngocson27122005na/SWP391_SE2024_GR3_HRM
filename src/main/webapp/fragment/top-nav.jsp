<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<nav class="top-nav">
    <div class="logo">
        <a href="#">MyWebApp</a>
    </div>
    <ul class="menu">
        <li><a href="#">Trang chủ</a></li>
        <li><a href="#">Giới thiệu</a></li>
        <li><a href="#">Liên hệ</a></li>
    </ul>
    <div class="auth">
        <!-- Kiểm tra session user -->
        <c:choose>
            <c:when test="${not empty sessionScope.account}">
                <span>Xin chào, ${sessionScope.account.username}</span>
                <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
            </c:otherwise>
        </c:choose>
    </div>
</nav>