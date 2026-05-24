<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chỉnh sửa hồ sơ</title>
</head>
<body>
<jsp:include page="/fragment/top-nav.jsp" />

<h2>Chỉnh sửa hồ sơ</h2>

<c:if test="${not empty error}">
    <p>${error}</p>
</c:if>

<form action="${pageContext.request.contextPath}/edit-profile" method="post">
    <table>
        <tr>
            <td>Họ tên:</td>
            <td><input type="text" name="fullName" value="${profile.fullName}" required /></td>
        </tr>
        <tr>
            <td>Email:</td>
            <td><input type="email" name="email" value="${profile.email}" /></td>
        </tr>
        <tr>
            <td>Điện thoại:</td>
            <td><input type="text" name="phone" value="${profile.phone}" /></td>
        </tr>
        <tr>
            <td>Địa chỉ:</td>
            <td><input type="text" name="address" value="${profile.address}" /></td>
        </tr>
        <tr>
            <td>Ngày sinh (yyyy-MM-dd):</td>
            <td><input type="text" name="dateOfBirth"
                       value="<fmt:formatDate value='${profile.dateOfBirth}' pattern='yyyy-MM-dd'/>" /></td>
        </tr>
        <tr>
            <td>Giới tính:</td>
            <td>
                <label><input type="radio" name="gender" value="1" <c:if test="${profile.gender}">checked</c:if> /> Nam</label>
                <label><input type="radio" name="gender" value="0" <c:if test="${profile.gender == false}">checked</c:if> /> Nữ</label>
            </td>
        </tr>
        <tr>
            <td>Hôn nhân:</td>
            <td>
                <label><input type="radio" name="married" value="1" <c:if test="${profile.married}">checked</c:if> /> Đã kết hôn</label>
                <label><input type="radio" name="married" value="0" <c:if test="${profile.married == false}">checked</c:if> /> Độc thân</label>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <button type="submit">Lưu</button>
                <a href="${pageContext.request.contextPath}/profile">Hủy</a>
            </td>
        </tr>
    </table>
</form>
</body>
</html>
