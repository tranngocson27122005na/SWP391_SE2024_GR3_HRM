<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>403 &middot; Không có quyền</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/error-page.css">
</head>
<body class="error-page">
<main class="error-card">
    <p class="error-card__code">403</p>
    <h1 class="error-card__title">Không có quyền truy cập</h1>
    <p class="error-card__desc">Bạn không được phép mở trang này.</p>
    <a class="auth-btn" href="${pageContext.request.contextPath}/home">Về trang chủ</a>
</main>
</body>
</html>
