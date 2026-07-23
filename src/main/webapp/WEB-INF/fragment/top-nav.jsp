<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<header class="top-nav">
    <div class="top-nav__left">
        <a href="${pageContext.request.contextPath}/home" class="top-nav__brand">HR<span>M</span></a>
    </div>

    <div class="top-nav__right">
        <c:choose>
            <c:when test="${not empty sessionScope.userSession}">
                <div class="top-nav__user">
                    <span class="top-nav__avatar">&#128100;</span>
                    <span class="top-nav__user-meta">
                        <strong>${sessionScope.userSession.username}</strong>
                        <c:if test="${not empty sessionScope.userSession.roles}">
                            <small>
                                <c:forEach var="role" items="${sessionScope.userSession.roles}" varStatus="st">
                                    ${role}<c:if test="${!st.last}">, </c:if>
                                </c:forEach>
                            </small>
                        </c:if>
                    </span>
                </div>
                <a href="${pageContext.request.contextPath}/change-password" class="top-nav__link">Đổi MK</a>
                <a href="${pageContext.request.contextPath}/logout" class="top-nav__link top-nav__link--danger">Đăng xuất</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login" class="top-nav__link">Đăng nhập</a>
            </c:otherwise>
        </c:choose>
    </div>
</header>
