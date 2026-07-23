<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:choose>
    <c:when test="${not empty sessionScope.userSession}">
        <c:redirect url="${pageContext.request.contextPath}/home"/>
    </c:when>
    <c:otherwise>
        <c:redirect url="${pageContext.request.contextPath}/login"/>
    </c:otherwise>
</c:choose>
