<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - HRM System</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: 'Segoe UI', sans-serif;
            background: #f0f2f5;
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
        }

        .card {
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 4px 20px rgba(0,0,0,.1);
            padding: 40px 36px;
            width: 100%;
            max-width: 400px;
        }

        .card h2 {
            text-align: center;
            margin-bottom: 28px;
            color: #1a1a2e;
            font-size: 1.6rem;
        }

        .form-group {
            margin-bottom: 18px;
        }

        .form-group label {
            display: block;
            margin-bottom: 6px;
            font-size: .9rem;
            color: #444;
            font-weight: 600;
        }

        .form-group input {
            width: 100%;
            padding: 10px 14px;
            border: 1px solid #ccc;
            border-radius: 6px;
            font-size: 1rem;
            transition: border-color .2s;
        }

        .form-group input:focus {
            outline: none;
            border-color: #4361ee;
        }

        .error-msg {
            background: #ffe5e5;
            color: #c0392b;
            border: 1px solid #f5c6c6;
            border-radius: 6px;
            padding: 10px 14px;
            margin-bottom: 16px;
            font-size: .9rem;
        }

        .btn-login {
            width: 100%;
            padding: 11px;
            background: #4361ee;
            color: #fff;
            border: none;
            border-radius: 6px;
            font-size: 1rem;
            font-weight: 600;
            cursor: pointer;
            transition: background .2s;
        }

        .btn-login:hover { background: #3451d1; }

        .footer-links {
            text-align: center;
            margin-top: 18px;
            font-size: .875rem;
        }

        .footer-links a {
            color: #4361ee;
            text-decoration: none;
        }

        .footer-links a:hover { text-decoration: underline; }
    </style>
</head>
<body>

<div class="card">
    <h2>🏢 HRM System</h2>

    <%-- Hiển thị lỗi nếu có --%>
    <% if (request.getAttribute("error") != null) { %>
    <div class="error-msg">${error}</div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <div class="form-group">
            <label for="username">Tên đăng nhập</label>
            <input type="text"
                   id="username"
                   name="username"
                   placeholder="Nhập tên đăng nhập"
                   value="${param.username}"
                   required autofocus>
        </div>

        <div class="form-group">
            <label for="password">Mật khẩu</label>
            <input type="password"
                   id="password"
                   name="password"
                   placeholder="Nhập mật khẩu"
                   required>
        </div>

        <button type="submit" class="btn-login">Đăng nhập</button>
    </form>

    <div class="footer-links">
        <a href="${pageContext.request.contextPath}/forgot-password">Quên mật khẩu?</a>
    </div>
</div>

</body>
</html>
