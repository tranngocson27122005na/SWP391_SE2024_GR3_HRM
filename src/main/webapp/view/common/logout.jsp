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
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/logout.css">

    <title>Đăng xuất</title>
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
