<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập &middot; HRM M&amp;T</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/login.css">
</head>
<body>
<div class="login-wrapper">
    <div class="login-left">
        <div class="brand-logo">HR<span>M</span></div>
        <h2>Hệ thống quản lý nhân sự<br>Công ty M&amp;T</h2>
        <p>Đăng nhập để sử dụng các chức năng nhân sự, tổ chức và quản trị hệ thống.</p>
        <div class="features">
            <div class="feature-item"><span class="icon">&#128101;</span> Quản lý tổ chức &amp; nhân viên</div>
            <div class="feature-item"><span class="icon">&#128197;</span> Chấm công &amp; nghỉ phép</div>
            <div class="feature-item"><span class="icon">&#128176;</span> Lương &amp; phiếu lương</div>
        </div>
    </div>

    <div class="login-right">
        <h3>Đăng nhập</h3>
        <p class="subtitle">Nhập tài khoản được cấp bởi quản trị viên</p>

        <c:if test="${not empty errorMessage}">
            <div class="alert-error">${errorMessage}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/login" autocomplete="on">
            <div class="form-group">
                <label for="username">Tên đăng nhập</label>
                <div class="input-icon-wrapper">
                    <span class="icon">&#128100;</span>
                    <input type="text" id="username" name="username"
                           value="${form.username}" placeholder="username" required autofocus>
                </div>
            </div>
            <div class="form-group">
                <label for="password">Mật khẩu</label>
                <div class="input-icon-wrapper">
                    <span class="icon">&#128274;</span>
                    <input type="password" id="password" name="password"
                           placeholder="••••••••" required>
                </div>
            </div>
            <button type="submit" class="btn-login">Đăng nhập</button>
        </form>

        <hr class="divider">
        <p class="footer-note">HRMS &copy; Công ty M&amp;T</p>
    </div>
</div>
</body>
</html>
