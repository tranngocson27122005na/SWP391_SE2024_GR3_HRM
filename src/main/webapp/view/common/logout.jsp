<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Hủy session ngay tại đây nếu controller chưa làm
    jakarta.servlet.http.HttpSession s = request.getSession(false);
    if (s != null) s.invalidate();
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <!-- Tự động chuyển về trang login sau 2 giây -->
    <meta http-equiv="refresh" content="2;url=${pageContext.request.contextPath}/login">
    <title>Đăng xuất</title>
</head>
<body>

<h2>Đã đăng xuất</h2>
<p>Đang chuyển về trang đăng nhập...</p>
<p><a href="${pageContext.request.contextPath}/login">Chuyển ngay</a></p>

</body>
</html>
