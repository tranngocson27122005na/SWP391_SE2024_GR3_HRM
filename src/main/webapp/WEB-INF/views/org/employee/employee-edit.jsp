<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sửa nhân viên &middot; HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/admin-table.css">
</head>
<body class="app-shell">
<jsp:include page="/WEB-INF/fragment/top-nav.jsp"/>
<main class="app-main app-main--solo">
    <h1 class="page-title">Sửa hồ sơ — ${emp.employeeCode}</h1>
    <div class="panel">
        <form method="post" action="${pageContext.request.contextPath}/employee/update">
            <input type="hidden" name="id" value="${emp.employeeId}">
            <label>Họ tên <input name="fullName" required value="${emp.fullName}"></label>
            <label>Giới tính
                <select name="gender" required>
                    <option value="1" ${emp.gender == 1 ? 'selected' : ''}>Nam</option>
                    <option value="2" ${emp.gender == 2 ? 'selected' : ''}>Nữ</option>
                    <option value="3" ${emp.gender == 3 ? 'selected' : ''}>Khác</option>
                </select>
            </label>
            <label>Ngày sinh <input type="date" name="birthDate" value="${birthDate}"></label>
            <label>TK ngân hàng <input name="bankAccount" value="${emp.bankAccount}"></label>
            <label>Chức vụ
                <select name="positionId" required>
                    <c:forEach var="p" items="${positions}">
                        <option value="${p.positionId}" ${emp.positionId == p.positionId ? 'selected' : ''}>
                            ${p.positionCode} — ${p.positionName}
                        </option>
                    </c:forEach>
                </select>
            </label>
            <label>Nhóm LĐ
                <select name="employmentGroup" required>
                    <option value="1" ${emp.employmentGroup == 1 ? 'selected' : ''}>Văn phòng</option>
                    <option value="2" ${emp.employmentGroup == 2 ? 'selected' : ''}>Nhà máy</option>
                </select>
            </label>
            <label>Ngày vào <input type="date" name="joiningDate" required value="${joiningDate}"></label>
            <p>
                <button class="btn" type="submit">Cập nhật</button>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/employee/detail?id=${emp.employeeId}">Hủy</a>
            </p>
        </form>
    </div>
</main>
</body>
</html>
