<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>${accountDetail != null ? "Chi tiết tài khoản" : "Thêm tài khoản mới"}</title>

  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/style/common/user-detail.css">
</head>
<body>

<div class="container">
  <h2>${accountDetail != null ? "Chi tiết tài khoản" : "Tạo tài khoản mới"}</h2>

  <c:if test="${not empty error}">
    <div class="error">${error}</div>
  </c:if>

  <form action="${pageContext.request.contextPath}/admin/user?action=add" method="POST">

    <div class="form-group">
      <label>Tên đăng nhập (Username):</label>
      <input type="text" name="username" value="${accountDetail.username}" ${accountDetail != null ? 'readonly' : ''} required>
    </div>

    <c:if test="${accountDetail == null}">
      <div class="form-group">
        <label>Mật khẩu (Password):</label>
        <input type="password" name="password" required>
      </div>
    </c:if>

    <div class="form-group">
      <label>Mã vai trò (Role ID):</label>
      <input type="number" name="roleId" value="${accountDetail.roleId}" ${accountDetail != null ? 'readonly' : ''} required>
    </div>

    <div class="form-group">
      <label>Mã công dân (Citizen ID):</label>
      <input type="number" name="citizenId" value="${accountDetail.citizenId}" ${accountDetail != null ? 'readonly' : ''}>
    </div>

    <div class="form-group">
      <label>Trạng thái hệ thống:</label>
      <input type="text" value="${accountDetail != null ? (accountDetail.isActive ? 'Đang hoạt động' : 'Bị khóa') : 'Tự động kích hoạt khi tạo mới'}" readonly>
    </div>

    <c:if test="${accountDetail == null}">
      <button type="submit" class="btn-submit">Lưu thông tin tài khoản</button>
    </c:if>

    <a href="${pageContext.request.contextPath}/admin/user?action=list" class="back-link">← Quay lại danh sách</a>
  </form>
</div>

</body>
</html>