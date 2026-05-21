<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách Vai trò</title>
</head>
<body>
<jsp:include page="/fragment/top-nav.jsp" />
<h2>Danh sách Vai trò</h2>

<table border="1" cellpadding="8" cellspacing="0">
    <thead>
    <tr>
        <th>Mã ID</th>
        <th>Tên Vai trò</th>
        <th>Trạng thái</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="role" items="${roles}">
        <tr>
            <td>${role.roleId}</td>
            <td>${role.roleName}</td>
            <td>
                <form action="${pageContext.request.contextPath}/role-list" method="post">
                    <input type="hidden" name="roleId" value="${role.roleId}" />

                    <label>
                        <input type="radio" name="isActive" value="true"
                               <c:if test="${role.isActive}">checked</c:if> />
                        Đang hoạt động
                    </label>

                    <label>
                        <input type="radio" name="isActive" value="false"
                               <c:if test="${!role.isActive}">checked</c:if> />
                        Không hoạt động
                    </label>

                    <button type="submit">Cập nhật</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

</body>
</html>
