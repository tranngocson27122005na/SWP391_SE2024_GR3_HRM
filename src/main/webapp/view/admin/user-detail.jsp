<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>${accountDetail != null ? "Chi tiết tài khoản" : "Thêm tài khoản mới"}</title>
  <style>
    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 40px; background-color: #f8f9fa; color: #333; }
    .container { max-width: 500px; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); margin: 0 auto; }
    h2 { margin-top: 0; color: #222; border-bottom: 2px solid #dee2e6; padding-bottom: 10px; margin-bottom: 20px; }
    .form-group { margin-bottom: 15px; }
    label { font-weight: 600; display: block; margin-bottom: 5px; font-size: 14px; }
    input { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; font-size: 14px; }
    input[readonly] { background-color: #e9ecef; color: #495057; cursor: not-allowed; }
    .btn-submit { background-color: #007bff; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; font-weight: bold; width: 100%; margin-top: 15px; font-size: 15px; }
    .btn-submit:hover { background-color: #0056b3; }
    .back-link { display: inline-block; margin-top: 20px; text-decoration: none; color: #6c757d; font-size: 14px; }
    .back-link:hover { color: #343a40; }
    .error { color: #dc3545; background-color: #f8d7da; border: 1px solid #f5c6cb; padding: 10px; border-radius: 4px; margin-bottom: 15px; font-size: 14px; }
  </style>
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