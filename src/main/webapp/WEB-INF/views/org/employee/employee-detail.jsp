<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết nhân viên &middot; HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/admin-table.css">
</head>
<body class="app-shell">
<jsp:include page="/WEB-INF/fragment/top-nav.jsp"/>
<main class="app-main app-main--solo">
    <h1 class="page-title">${employee.fullName}</h1>
    <p class="page-subtitle">${employee.employeeCode}
        — ${employee.working ? 'Đang làm' : 'Chưa có HĐ / Đã nghỉ'}
        <c:if test="${not empty dependentCount}"> — NPT ACTIVE: ${dependentCount}</c:if>
    </p>

    <div class="panel">
        <dl class="detail-grid">
            <dt>Giới tính</dt><dd>${employee.genderLabel}</dd>
            <dt>Ngày sinh</dt><dd><fmt:formatDate value="${employee.birthDate}" pattern="dd/MM/yyyy"/></dd>
            <dt>Tài khoản ngân hàng</dt><dd>${empty employee.bankAccount ? '—' : employee.bankAccount}</dd>
            <dt>Chức vụ</dt><dd>${employee.positionName}</dd>
            <dt>Phòng ban</dt><dd>${employee.departmentName}</dd>
            <dt>Nhóm lao động</dt><dd>${employee.employmentGroupLabel}</dd>
            <dt>Ngày vào</dt><dd><fmt:formatDate value="${employee.joiningDate}" pattern="dd/MM/yyyy"/></dd>
            <dt>HĐ hiện hành</dt>
            <dd>
                <c:choose>
                    <c:when test="${empty employee.currentContractId}">
                        <span class="flash-error">Chưa có hợp đồng</span>
                    </c:when>
                    <c:otherwise>#${employee.currentContractId}</c:otherwise>
                </c:choose>
            </dd>
        </dl>
        <c:if test="${empty employee.currentContractId and canContractCreate and not profileSelf}">
            <p class="page-subtitle" style="margin-top:12px">
                Nhân viên chưa có HĐ hiện hành — tạo HĐ đầu để chuyển sang đang làm.
                <a class="btn" href="${pageContext.request.contextPath}/contract/create?employeeId=${employee.employeeId}">Tạo hợp đồng đầu</a>
            </p>
        </c:if>
        <p style="margin-top:20px">
            <c:choose>
                <c:when test="${profileSelf}">
                    <c:if test="${canDependentRead}">
                        <a class="btn" href="${pageContext.request.contextPath}/dependent/list?employeeId=${employee.employeeId}">Người phụ thuộc</a>
                    </c:if>
                    <c:if test="${canContractRead}">
                        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/contract/list?employeeId=${employee.employeeId}">Hợp đồng</a>
                    </c:if>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/home">Về trang chủ</a>
                </c:when>
                <c:otherwise>
                    <c:if test="${canUpdate}">
                        <a class="btn" href="${pageContext.request.contextPath}/employee/edit?id=${employee.employeeId}">Sửa</a>
                    </c:if>
                    <c:if test="${canContractCreate and empty employee.currentContractId}">
                        <a class="btn" href="${pageContext.request.contextPath}/contract/create?employeeId=${employee.employeeId}">Tạo HĐ đầu</a>
                    </c:if>
                    <c:if test="${canContractRead}">
                        <a class="btn" href="${pageContext.request.contextPath}/contract/list?employeeId=${employee.employeeId}">Hợp đồng</a>
                    </c:if>
                    <c:if test="${canDependentRead}">
                        <a class="btn" href="${pageContext.request.contextPath}/dependent/list?employeeId=${employee.employeeId}">NPT</a>
                    </c:if>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/employee/list">Danh sách</a>
                </c:otherwise>
            </c:choose>
        </p>
    </div>
</main>
</body>
</html>
