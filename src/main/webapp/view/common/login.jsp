<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập – HRM System</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/login.css">
</head>
<body>

<div class="login-wrapper">

    <!-- LEFT PANEL -->
    <div class="login-left">
        <div class="brand-logo">HRM <span>System</span></div>
        <h2>Chào mừng trở lại! 👋</h2>
        <p>Hệ thống quản lý nhân sự toàn diện – giúp bạn quản lý nhân viên, phân quyền và hồ sơ một cách hiệu quả.</p>
        <div class="features">
            <div class="feature-item">
                <div class="icon">👤</div>
                <span>Quản lý tài khoản & phân quyền</span>
            </div>
            <div class="feature-item">
                <div class="icon">📋</div>
                <span>Xem và chỉnh sửa hồ sơ cá nhân</span>
            </div>
            <div class="feature-item">
                <div class="icon">🔒</div>
                <span>Bảo mật dữ liệu theo vai trò</span>
            </div>
        </div>
    </div>

    <!-- RIGHT PANEL -->
    <div class="login-right">
        <h3>Đăng nhập</h3>
        <p class="subtitle">Nhập thông tin tài khoản của bạn để tiếp tục</p>

        <!-- Hiển thị lỗi nếu có -->
        <c:if test="${not empty error}">
            <div class="alert-error">
                ⚠️ ${error}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">

            <div class="form-group">
                <label for="username">Tên đăng nhập</label>
                <div class="input-icon-wrapper">
                    <span class="icon">👤</span>
                    <input type="text" id="username" name="username"
                           placeholder="Nhập tên đăng nhập" required autocomplete="username" />
                </div>
            </div>

            <div class="form-group">
                <label for="password">Mật khẩu</label>
                <div class="input-icon-wrapper">
                    <span class="icon">🔑</span>
                    <input type="password" id="password" name="password"
                           placeholder="Nhập mật khẩu" required autocomplete="current-password" />
                </div>
            </div>

            <button type="submit" class="btn-login">Đăng nhập →</button>
        </form>

        <hr class="divider">
        <p class="footer-note">© 2024 HRM System – SWP391 SE2024 GR3</p>
    </div>

</div>
</body>
</html>
