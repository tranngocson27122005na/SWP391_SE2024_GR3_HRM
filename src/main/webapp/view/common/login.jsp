<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập – HRM System</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: 'Inter', sans-serif;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(135deg, #1e3a8a 0%, #1d4ed8 50%, #3b82f6 100%);
            padding: 20px;
        }

        .login-wrapper {
            display: flex;
            width: 100%;
            max-width: 900px;
            min-height: 520px;
            border-radius: 20px;
            overflow: hidden;
            box-shadow: 0 25px 60px rgba(0, 0, 0, 0.35);
        }

        /* ===== LEFT PANEL ===== */
        .login-left {
            flex: 1;
            background: linear-gradient(160deg, #1e40af, #1d4ed8, #2563eb);
            padding: 50px 40px;
            display: flex;
            flex-direction: column;
            justify-content: center;
            color: white;
            position: relative;
            overflow: hidden;
        }

        .login-left::before {
            content: '';
            position: absolute;
            top: -80px; right: -80px;
            width: 260px; height: 260px;
            background: rgba(255,255,255,0.06);
            border-radius: 50%;
        }

        .login-left::after {
            content: '';
            position: absolute;
            bottom: -60px; left: -60px;
            width: 200px; height: 200px;
            background: rgba(255,255,255,0.06);
            border-radius: 50%;
        }

        .brand-logo {
            font-size: 28px;
            font-weight: 700;
            letter-spacing: -0.5px;
            margin-bottom: 40px;
        }

        .brand-logo span {
            background: rgba(255,255,255,0.25);
            padding: 2px 10px;
            border-radius: 8px;
        }

        .login-left h2 {
            font-size: 26px;
            font-weight: 700;
            line-height: 1.4;
            margin-bottom: 14px;
        }

        .login-left p {
            font-size: 14px;
            color: rgba(255,255,255,0.75);
            line-height: 1.7;
        }

        .features {
            margin-top: 36px;
            display: flex;
            flex-direction: column;
            gap: 14px;
        }

        .feature-item {
            display: flex;
            align-items: center;
            gap: 12px;
            font-size: 13px;
            color: rgba(255,255,255,0.85);
        }

        .feature-item .icon {
            width: 30px; height: 30px;
            background: rgba(255,255,255,0.15);
            border-radius: 8px;
            display: flex; align-items: center; justify-content: center;
            font-size: 15px;
            flex-shrink: 0;
        }

        /* ===== RIGHT PANEL ===== */
        .login-right {
            flex: 1;
            background: #ffffff;
            padding: 50px 45px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        .login-right h3 {
            font-size: 22px;
            font-weight: 700;
            color: #111827;
            margin-bottom: 6px;
        }

        .login-right .subtitle {
            font-size: 13px;
            color: #6b7280;
            margin-bottom: 32px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            font-size: 13px;
            font-weight: 600;
            color: #374151;
            margin-bottom: 7px;
        }

        .form-group input {
            width: 100%;
            padding: 11px 14px;
            border: 1.5px solid #e5e7eb;
            border-radius: 10px;
            font-size: 14px;
            font-family: 'Inter', sans-serif;
            color: #111827;
            background: #f9fafb;
            transition: border-color 0.2s, box-shadow 0.2s, background 0.2s;
            outline: none;
        }

        .form-group input:focus {
            border-color: #2563eb;
            background: #fff;
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.12);
        }

        .form-group input::placeholder {
            color: #9ca3af;
        }

        .input-icon-wrapper {
            position: relative;
        }

        .input-icon-wrapper .icon {
            position: absolute;
            left: 13px;
            top: 50%;
            transform: translateY(-50%);
            color: #9ca3af;
            font-size: 16px;
            pointer-events: none;
        }

        .input-icon-wrapper input {
            padding-left: 38px;
        }

        .btn-login {
            width: 100%;
            padding: 12px;
            background: linear-gradient(135deg, #1d4ed8, #2563eb);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 15px;
            font-weight: 600;
            font-family: 'Inter', sans-serif;
            cursor: pointer;
            transition: opacity 0.2s, transform 0.15s, box-shadow 0.2s;
            margin-top: 8px;
            box-shadow: 0 4px 14px rgba(37, 99, 235, 0.35);
        }

        .btn-login:hover {
            opacity: 0.92;
            transform: translateY(-1px);
            box-shadow: 0 6px 18px rgba(37, 99, 235, 0.45);
        }

        .btn-login:active {
            transform: translateY(0);
        }

        /* ===== ERROR MESSAGE ===== */
        .alert-error {
            display: flex;
            align-items: center;
            gap: 10px;
            background: #fef2f2;
            border: 1px solid #fecaca;
            color: #dc2626;
            border-radius: 10px;
            padding: 12px 15px;
            font-size: 13px;
            font-weight: 500;
            margin-bottom: 20px;
            animation: shake 0.4s ease;
        }

        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            25% { transform: translateX(-6px); }
            75% { transform: translateX(6px); }
        }

        .divider {
            border: none;
            border-top: 1px solid #f3f4f6;
            margin: 24px 0 18px;
        }

        .footer-note {
            text-align: center;
            font-size: 12px;
            color: #9ca3af;
        }

        /* ===== RESPONSIVE ===== */
        @media (max-width: 640px) {
            .login-left { display: none; }
            .login-right { padding: 40px 28px; border-radius: 20px; }
            .login-wrapper { border-radius: 20px; }
        }
    </style>
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
