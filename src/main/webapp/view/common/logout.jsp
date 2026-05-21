<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Invalidate session ngay tại đây nếu controller chưa làm
    jakarta.servlet.http.HttpSession s = request.getSession(false);
    if (s != null) s.invalidate();
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="refresh" content="2;url=${pageContext.request.contextPath}/login">
    <title>Đăng xuất</title>
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

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
            border: 1px solid #e2e0d8;
            padding: 40px 36px;
            width: 100%;
            max-width: 360px;
            text-align: center;
        }

        .icon {
            font-size: 2.5rem;
            margin-bottom: 16px;
        }

        h2 {
            font-size: 1.2rem;
            font-weight: 600;
            color: #1a1917;
            margin-bottom: 8px;
        }

        p {
            font-size: .875rem;
            color: #6b6860;
            margin-bottom: 24px;
        }

        .progress-bar {
            height: 3px;
            background: #e2e0d8;
            border-radius: 10px;
            overflow: hidden;
            margin-bottom: 16px;
        }

        .progress-fill {
            height: 100%;
            background: #2563eb;
            border-radius: 10px;
            width: 0%;
            animation: fill 2s linear forwards;
        }

        @keyframes fill { to { width: 100%; } }

        a {
            font-size: .8rem;
            color: #2563eb;
            text-decoration: none;
        }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>
<jsp:include page="/fragment/top-nav.jsp" />
<div class="card">
    <div class="icon">👋</div>
    <h2>Đã đăng xuất</h2>
    <p>Đang chuyển về trang đăng nhập...</p>
    <div class="progress-bar">
        <div class="progress-fill"></div>
    </div>
    <a href="${pageContext.request.contextPath}/login">Chuyển ngay</a>
</div>

</body>
</html>
