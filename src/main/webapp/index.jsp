<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%
    jakarta.servlet.http.HttpSession s = request.getSession();
    com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account account =
            (com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account) s.getAttribute("account");

    if (account == null) {
        account = new com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account();
        account.setUsername("Khách tham quan");
        account.setRoleId(0); // role giả
        s.setAttribute("account", account);
    }
%>


<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Trang giới thiệu – MyWebApp</title>
</head>
<body>
<jsp:include page="/fragment/top-nav.jsp"/>

<h1>Chào mừng đến với MyWebApp</h1>

<c:choose>
    <c:when test="${not empty user}">
        <!-- Nếu đã đăng nhập -->
        <p>Xin chào, ${user.username}!</p>
        <p>Bạn có thể truy cập <a href="${pageContext.request.contextPath}/home">Trang chủ</a> để xem chi tiết thông tin công ty.</p>
        <p><a href="${pageContext.request.contextPath}/logout">Đăng xuất</a></p>
    </c:when>
    <c:otherwise>
        <!-- Nếu chưa đăng nhập -->
        <h3>Thông tin cơ bản về công ty</h3>
        <ul>
            <li>Thành lập năm 2020</li>
            <li>Lĩnh vực: Quản lý nhân sự</li>
            <li>Trụ sở: Hà Nội</li>
        </ul>
        <p>Để xem chi tiết hồ sơ công ty và các dịch vụ, vui lòng <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>.</p>
    </c:otherwise>
</c:choose>

</body>
</html>
