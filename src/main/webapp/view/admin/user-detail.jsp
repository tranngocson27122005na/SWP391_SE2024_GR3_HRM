<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>
        <c:choose>
            <c:when test="${not empty accountDetail.accountId}">Chi tiết tài khoản</c:when>
            <c:otherwise>Thêm tài khoản mới</c:otherwise>
        </c:choose>
    </title>
</head>
<body>
<jsp:include page="/fragment/top-nav.jsp" />

<h2>
    <c:choose>
        <c:when test="${not empty accountDetail.accountId}">Chi tiết / cập nhật tài khoản</c:when>
        <c:otherwise>Tạo tài khoản mới</c:otherwise>
    </c:choose>
</h2>

<c:if test="${not empty error}">
    <p>${error}</p>
</c:if>
<c:if test="${param.success == 'updated'}">
    <p>Cập nhật tài khoản thành công.</p>
</c:if>

<c:choose>
    <c:when test="${not empty accountDetail.accountId}">
        <form action="${pageContext.request.contextPath}/admin/user" method="post">
            <input type="hidden" name="action" value="update" />
            <input type="hidden" name="accountId" value="${accountDetail.accountId}" />
            <input type="hidden" name="hiddenCitizenId" value="${accountDetail.citizenId}" />
            <table>
                <tr><td>Username:</td><td>${accountDetail.username}</td></tr>
                <tr><td>Citizen ID:</td><td>${accountDetail.citizenId}</td></tr>
                <tr>
                    <td>Vai trò:</td>
                    <td>
                        <select name="roleId" required>
                            <c:forEach items="${activeRoles}" var="r">
                                <option value="${r.roleId}" <c:if test="${r.roleId == accountDetail.roleId}">selected</c:if>>
                                    ${r.roleName}
                                </option>
                            </c:forEach>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>Họ tên:</td>
                    <td><input type="text" name="fullName" value="${accountDetail.fullName}" required /></td>
                </tr>
                <tr>
                    <td>Email:</td>
                    <td><input type="email" name="email" value="${accountDetail.email}" /></td>
                </tr>
                <tr>
                    <td>Điện thoại:</td>
                    <td><input type="text" name="phone" value="${accountDetail.phone}" /></td>
                </tr>
                <tr>
                    <td>Địa chỉ:</td>
                    <td><input type="text" name="address" value="${accountDetail.address}" /></td>
                </tr>
                <tr>
                    <td>Ngày sinh:</td>
                    <td><input type="text" name="dateOfBirth"
                               value="<fmt:formatDate value='${accountDetail.dateOfBirth}' pattern='yyyy-MM-dd'/>" placeholder="yyyy-MM-dd" /></td>
                </tr>
                <tr>
                    <td>Trạng thái:</td>
                    <td>
                        <c:choose>
                            <c:when test="${accountDetail.isActive}">Đang hoạt động</c:when>
                            <c:otherwise>Bị khóa</c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <button type="submit">Lưu thay đổi</button>
                        <a href="${pageContext.request.contextPath}/admin/user-list">Quay lại</a>
                    </td>
                </tr>
            </table>
        </form>
    </c:when>
    <c:otherwise>
        <form action="${pageContext.request.contextPath}/admin/user" method="post">
            <input type="hidden" name="action" value="add" />
            <table>
                <tr>
                    <td>Username:</td>
                    <td><input type="text" name="username" value="${accountDetail.username}" required /></td>
                </tr>
                <tr>
                    <td>Mật khẩu:</td>
                    <td><input type="password" name="password" required /></td>
                </tr>
                <tr>
                    <td>Xác nhận mật khẩu:</td>
                    <td><input type="password" name="confirmPassword" required /></td>
                </tr>
                <tr>
                    <td>Vai trò:</td>
                    <td>
                        <select name="roleId" required>
                            <option value="">-- Chọn vai trò --</option>
                            <c:forEach items="${activeRoles}" var="r">
                                <option value="${r.roleId}" <c:if test="${r.roleId == accountDetail.roleId}">selected</c:if>>
                                    ${r.roleName}
                                </option>
                            </c:forEach>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>Citizen ID:</td>
                    <td><input type="number" name="citizenId" value="${accountDetail.citizenId}" required /></td>
                </tr>
                <tr>
                    <td>Họ tên:</td>
                    <td><input type="text" name="fullName" value="${accountDetail.fullName}" /></td>
                </tr>
                <tr>
                    <td>Email:</td>
                    <td><input type="email" name="email" value="${accountDetail.email}" /></td>
                </tr>
                <tr>
                    <td>Điện thoại:</td>
                    <td><input type="text" name="phone" value="${accountDetail.phone}" /></td>
                </tr>
                <tr>
                    <td colspan="2">
                        <button type="submit">Tạo tài khoản</button>
                        <a href="${pageContext.request.contextPath}/admin/user-list">Quay lại</a>
                    </td>
                </tr>
            </table>
        </form>
    </c:otherwise>
</c:choose>
</body>
</html>
