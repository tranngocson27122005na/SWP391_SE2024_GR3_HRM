<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Danh sách Tài khoản</title>
</head>
<body>
<jsp:include page="/fragment/top-nav.jsp" />
<h2>Danh sách tài khoản hệ thống</h2>
<a href="${pageContext.request.contextPath}/admin/user?action=detail&id=0" class="btn"> + Thêm tài khoản mới</a>

<table>
    <thead>
    <tr>
        <th>Mã tài khoản (ID)</th>
        <th>Tên đăng nhập (Username)</th>
        <th>Mã vai trò (Role ID)</th>
        <th>Mã công dân (Citizen ID)</th>
        <th>Trạng thái</th>
        <th>Hành động</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${userList}" var="user">
        <tr>
            <td>${user.accountId}</td>
            <td><strong>${user.username}</strong></td>
            <td>${user.roleId}</td>
            <td>${user.citizenId != null ? user.citizenId : "Chưa liên kết"}</td>
            <td>
            <form action="${pageContext.request.contextPath}/admin/user" method="post">
                <input type="hidden" name="accountId" value="${user.accountId}" />

                <label>
                    <input type="radio" name="isActive" value="true"
                           <c:if test="${user.isActive}">checked</c:if> />
                    Đang hoạt động
                </label>

                <label>
                    <input type="radio" name="isActive" value="false"
                           <c:if test="${!user.isActive}">checked</c:if> />
                    Bị khóa
                </label>

                <button type="submit">Cập nhật</button>
            </form>
        </td>
            <td>
                <a href="${pageContext.request.contextPath}/admin/user?action=detail&id=${user.accountId}" class="btn btn-view">Xem chi tiết</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>