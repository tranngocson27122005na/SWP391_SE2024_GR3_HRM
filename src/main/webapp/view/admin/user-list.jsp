<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quản lý Tài Khoản</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 40px; background-color: #f8f9fa; color: #333; }
        h2 { color: #222; margin-bottom: 20px; }
        .btn { padding: 8px 16px; text-decoration: none; background-color: #007bff; color: white; border-radius: 4px; display: inline-block; font-weight: 600; font-size: 14px; }
        .btn-view { background-color: #28a745; padding: 4px 8px; font-size: 13px; }
        .btn:hover { opacity: 0.9; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; background: white; box-shadow: 0 2px 5px rgba(0,0,0,0.05); border-radius: 8px; overflow: hidden; }
        th, td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #dee2e6; }
        th { background-color: #343a40; color: white; font-weight: 600; }
        tr:hover { background-color: #f1f3f5; }
        .status-active { color: #28a745; font-weight: bold; }
        .status-locked { color: #dc3545; font-weight: bold; }
        .pagination { margin-top: 20px; display: flex; gap: 10px; align-items: center; }
        .pagination a { text-decoration: none; color: #007bff; font-weight: bold; }
    </style>
</head>
<body>

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
                <c:choose>
                    <c:when test="${user.isActive}">
                        <span class="status-active">Đang hoạt động</span>
                    </c:when>
                    <c:otherwise>
                        <span class="status-locked">Bị khóa</span>
                    </c:otherwise>
                </c:choose>
            </td>
            <td>
                <a href="${pageContext.request.contextPath}/admin/user?action=detail&id=${user.accountId}" class="btn btn-view">Xem chi tiết</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<div class="pagination">
    <c:if test="${currentPage > 1}">
        <a href="user?action=list&page=${currentPage - 1}">« Trang trước</a>
    </c:if>
    <span>Trang ${currentPage}</span>
    <a href="user?action=list&page=${currentPage + 1}">Trang sau »</a>
</div>

</body>
</html>