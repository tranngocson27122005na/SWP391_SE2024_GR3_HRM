<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quên mật khẩu – HRM System</title>
<%--    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/login.css">--%>
</head>
<body>
<div class="forgot-wrapper">
    <h3>Quên mật khẩu</h3>
    <p>Nhập email đã đăng ký để nhận liên kết đặt lại mật khẩu.</p>

    <form action="${pageContext.request.contextPath}/forgot-password" method="post">
        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" name="email"
                   placeholder="Nhập email của bạn" required />
        </div>
        <button type="submit" class="btn-login">Gửi liên kết đặt lại</button>
    </form>

    <c:if test="${not empty message}">
        <div class="alert-success">${message}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert-error">${error}</div>
    </c:if>
</div>
</body>
</html>
