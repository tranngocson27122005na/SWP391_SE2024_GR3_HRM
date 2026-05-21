<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<style>
    /* CSS nhỏ gọn cho top-nav */
    .top-nav {
        background: #ffffff;
        border-bottom: 1px solid #e6e6e6;
        padding: 10px 20px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        font-family: 'DM Sans', sans-serif;
    }
    .top-nav .left, .top-nav .right { display:flex; align-items:center; gap:12px; }
    .top-nav a { color:#1a1917; text-decoration:none; padding:6px 10px; border-radius:6px; }
    .top-nav a:hover { background:#f3f4f6; }
    .top-nav .btn { background:#2563eb; color:#fff; padding:6px 10px; border-radius:6px; }
    .top-nav .avatar {
        width:32px; height:32px; border-radius:50%; background:#2563eb; color:#fff;
        display:flex; align-items:center; justify-content:center; font-weight:600;
    }
</style>

<nav class="top-nav" role="navigation" aria-label="Top navigation">
    <div class="left">
        <a href="${pageContext.request.contextPath}/" class="brand">HRM System</a>
        <a href="${pageContext.request.contextPath}/home">Home</a>
    </div>

    <div class="right">
        <!-- Nếu chưa đăng nhập -->
        <c:if test="${empty sessionScope.account}">
            <a href="${pageContext.request.contextPath}/login" class="btn">Sign In</a>
        </c:if>

        <!-- Nếu đã đăng nhập -->
        <c:if test="${not empty sessionScope.account}">
            <a href="${pageContext.request.contextPath}/profile" title="Profile">
        <span class="avatar">
          <c:out value="${fn:toUpperCase(sessionScope.account.username.substring(0,1))}" />
        </span>
            </a>
<%--            <a href="${pageContext.request.contextPath}/profile">--%>
<%--                <c:out value="${sessionScope.account.username}" />--%>
<%--            </a>--%>
<%--            <a href="${pageContext.request.contextPath}/logout">Logout</a>--%>
        </c:if>
    </div>
</nav>
