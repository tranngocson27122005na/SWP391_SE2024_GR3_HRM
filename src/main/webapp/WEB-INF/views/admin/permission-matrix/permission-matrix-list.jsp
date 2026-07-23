<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ma trận phân quyền &middot; HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/admin-table.css">
</head>
<body class="app-shell">
<jsp:include page="/WEB-INF/fragment/top-nav.jsp"/>
<main class="app-main app-main--solo">
    <h1 class="page-title">Ma trận phân quyền</h1>
    <p class="page-subtitle">Hàng = chức năng (permission) · Cột = chức vụ (position)</p>

    <c:if test="${not empty successMessage}">
        <div class="flash flash--ok">${successMessage}</div>
    </c:if>
    <c:if test="${not empty sessionScope.flashError}">
        <div class="flash flash--error">${sessionScope.flashError}</div>
        <c:remove var="flashError" scope="session"/>
    </c:if>

    <div class="panel matrix-wrap">
        <form method="post" action="${pageContext.request.contextPath}/permission-matrix/update">
            <div class="toolbar">
                <button class="btn btn-primary" type="submit">Lưu ma trận</button>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/home">Về trang chủ</a>
            </div>
            <div class="matrix-scroll">
                <table class="data-table matrix-table">
                    <thead>
                    <tr>
                        <th class="matrix-sticky">Chức năng</th>
                        <c:forEach var="pos" items="${matrix.positions}">
                            <th class="center" title="${pos.positionName} · ${pos.departmentName} · ${pos.dataScopeLabel}">
                                ${pos.positionCode}
                                <br><small>${pos.positionName}</small>
                                <br><small class="matrix-scope">${pos.dataScopeLabel}</small>
                            </th>
                        </c:forEach>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="p" items="${matrix.permissions}">
                        <tr>
                            <td class="matrix-sticky" title="${p.permissionName}">
                                ${p.permissionName}
                                <br><small>${p.resource} · ${p.actionLabel}</small>
                            </td>
                            <c:forEach var="pos" items="${matrix.positions}">
                                <c:set var="key" value="${pos.positionId}_${p.permissionId}"/>
                                <td class="center">
                                    <input type="checkbox" name="assignment" value="${key}"
                                        <c:if test="${matrix.grantedKeys.contains(key)}">checked</c:if>>
                                </td>
                            </c:forEach>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty matrix.permissions}">
                        <tr><td colspan="${fn:length(matrix.positions) + 1}">Không có permission trong catalog.</td></tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </form>
    </div>
</main>
</body>
</html>
