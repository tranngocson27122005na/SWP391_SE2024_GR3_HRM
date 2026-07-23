<%--
  Shared list pager (POST về URL danh sách — không dùng PagingController).

  Request attributes:
    - paging (required): com.hrm.utility.Paging sau calc()
    - pagingAction (optional): form action; mặc định = requestURI hiện tại
    - keyword / statusFilter: giữ filter phổ biến qua hidden

  Nút: Đầu(Home) / Trước(Pre) / số trang 1-based(name=page) / Sau(Next) / Cuối(End)
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<c:if test="${paging != null && paging.totalPage >= 1}">
    <c:set var="pagerAction"
           value="${not empty pagingAction ? pagingAction : pageContext.request.requestURI}"/>
    <form class="pager" method="post" action="${pagerAction}">
        <input type="hidden" name="index" value="${paging.index}">

        <c:if test="${not empty keyword}">
            <input type="hidden" name="keyword" value="${keyword}">
        </c:if>
        <c:if test="${empty keyword && not empty param.keyword}">
            <input type="hidden" name="keyword" value="${param.keyword}">
        </c:if>
        <c:if test="${statusFilter != null}">
            <input type="hidden" name="status" value="${statusFilter}">
        </c:if>
        <c:if test="${statusFilter == null && not empty param.status}">
            <input type="hidden" name="status" value="${param.status}">
        </c:if>

        <c:if test="${paging.totalPage > 1}">
            <button class="btn btn-secondary" type="submit" name="Home" value="Home"
                    <c:if test="${paging.index <= 0}">disabled</c:if>>Đầu</button>
            <button class="btn btn-secondary" type="submit" name="Pre" value="Pre"
                    <c:if test="${paging.index <= 0}">disabled</c:if>>Trước</button>

            <c:forEach begin="${paging.pageStart}" end="${paging.pageEnd}" var="i">
                <button type="submit" name="page" value="${i + 1}"
                        class="btn ${i == paging.index ? 'btn-primary' : 'btn-secondary'}">${i + 1}</button>
            </c:forEach>

            <button class="btn btn-secondary" type="submit" name="Next" value="Next"
                    <c:if test="${paging.index + 1 >= paging.totalPage}">disabled</c:if>>Sau</button>
            <button class="btn btn-secondary" type="submit" name="End" value="End"
                    <c:if test="${paging.index + 1 >= paging.totalPage}">disabled</c:if>>Cuối</button>
        </c:if>

        <span class="pager__meta">Trang ${paging.displayIndex} / ${paging.totalPage} · ${paging.nrpp}/trang</span>
    </form>
</c:if>
