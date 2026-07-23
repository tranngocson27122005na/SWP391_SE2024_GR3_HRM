<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thêm hợp đồng &middot; HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/admin-table.css">
</head>
<body class="app-shell">
<jsp:include page="/WEB-INF/fragment/top-nav.jsp"/>
<main class="app-main app-main--solo">
    <h1 class="page-title">${firstContract ? 'Tạo hợp đồng đầu' : 'Thêm / chuyển hợp đồng'}</h1>
    <c:if test="${not empty employeeName}">
        <p class="page-subtitle">${employeeName}<c:if test="${not empty employeeCode}"> (${employeeCode})</c:if></p>
    </c:if>
    <c:if test="${firstContract}">
        <p class="page-subtitle">Nhân viên chưa có HĐ hiện hành — HĐ mới sẽ ACTIVE và gắn làm HĐ hiện hành.</p>
    </c:if>
    <c:if test="${not empty errorMessage}"><p class="flash-error">${errorMessage}</p></c:if>
    <div class="panel">
        <form method="post" action="${pageContext.request.contextPath}/contract/create">
            <input type="hidden" name="employeeId" value="${employeeId}">
            <label>Loại
                <select name="contractType" required>
                    <option value="1" ${con.contractType == 1 ? 'selected' : ''}>Thử việc</option>
                    <option value="2" ${empty con.contractType or con.contractType == 2 ? 'selected' : ''}>Chính thức</option>
                </select>
            </label>
            <label>Ngày bắt đầu <input type="date" name="startDate" required value="${con.startDate}"></label>
            <label>Ngày kết thúc <input type="date" name="endDate" value="${con.endDate}"></label>
            <label>Lương cơ bản <input name="basicSalary" required value="${empty con.basicSalary ? '7500000' : con.basicSalary}"></label>
            <label>Hình thức
                <select name="salaryType" required>
                    <option value="1" ${empty con.salaryType or con.salaryType == 1 ? 'selected' : ''}>Theo tháng</option>
                    <option value="2" ${con.salaryType == 2 ? 'selected' : ''}>Theo giờ</option>
                </select>
            </label>
            <p>
                <button class="btn" type="submit">
                    ${firstContract ? 'Lưu hợp đồng đầu' : 'Lưu (HĐ ACTIVE cũ → INACTIVE)'}
                </button>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/contract/list?employeeId=${employeeId}">Hủy</a>
            </p>
        </form>
    </div>
</main>
</body>
</html>
