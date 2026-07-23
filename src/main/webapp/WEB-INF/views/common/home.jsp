<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trang chủ &middot; HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/home-hub.css">
</head>
<body class="app-shell home-hub">
<jsp:include page="/WEB-INF/fragment/top-nav.jsp"/>
<main class="hub-main">
    <header class="hub-header">
        <h1 class="page-title">Xin chào, ${user.username}</h1>
        <p class="page-subtitle">Chọn chức năng để tiếp tục</p>
        <div class="hub-search">
            <input type="search" id="hubSearch" placeholder="Tìm chức năng..." autocomplete="off"
                   aria-label="Tìm chức năng">
        </div>
    </header>

    <div class="hub-grid" id="hubGrid">
        <c:forEach var="tile" items="${tiles}">
            <a class="hub-tile" href="${pageContext.request.contextPath}${tile.url}"
               data-label="${tile.label}">
                <span class="hub-tile__icon hub-tile__icon--${tile.iconKey}" aria-hidden="true"></span>
                <span class="hub-tile__label">${tile.label}</span>
            </a>
        </c:forEach>
        <c:if test="${empty tiles}">
            <p class="hub-empty">Chưa có chức năng nào được cấp quyền.</p>
        </c:if>
    </div>
</main>
<script>
    (function () {
        var input = document.getElementById('hubSearch');
        var grid = document.getElementById('hubGrid');
        if (!input || !grid) return;
        input.addEventListener('input', function () {
            var q = (input.value || '').trim().toLowerCase();
            grid.querySelectorAll('.hub-tile').forEach(function (tile) {
                var label = (tile.getAttribute('data-label') || '').toLowerCase();
                tile.style.display = (!q || label.indexOf(q) >= 0) ? '' : 'none';
            });
        });
    })();
</script>
</body>
</html>
