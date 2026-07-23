<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đổi mật khẩu giúp &middot; HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/auth-form.css">
</head>
<body class="app-shell">
<jsp:include page="/WEB-INF/fragment/top-nav.jsp"/>
<main class="auth-form-page">
    <div class="auth-form-card">
        <h1 class="page-title">Đổi mật khẩu giúp</h1>
        <p class="page-subtitle">Tài khoản: <strong>${username}</strong></p>

        <c:if test="${not empty errorMessage}">
            <div class="auth-alert" role="alert">${errorMessage}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/sys-user/reset-password" class="auth-form">
            <input type="hidden" name="userId" value="${userId}">
            <input type="hidden" name="username" value="${username}">
            <label class="auth-form__field">
                <span>Mật khẩu mới</span>
                <input type="password" name="newPassword" required minlength="6" autocomplete="new-password">
            </label>
            <label class="auth-form__field">
                <span>Xác nhận mật khẩu</span>
                <input type="password" name="confirmPassword" required minlength="6" autocomplete="new-password">
            </label>
            <div class="auth-form__actions">
                <button type="submit" class="auth-btn">Lưu</button>
                <a class="auth-btn auth-btn--ghost" href="${pageContext.request.contextPath}/sys-user/list">Hủy</a>
            </div>
        </form>
    </div>
</main>
</body>
</html>
