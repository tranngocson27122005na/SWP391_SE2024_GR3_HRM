<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo nhân viên &middot; HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style/common/admin-table.css">
</head>
<body class="app-shell">
<jsp:include page="/WEB-INF/fragment/top-nav.jsp"/>
<main class="app-main app-main--solo">
    <h1 class="page-title">Tạo nhân viên + hợp đồng đầu</h1>
    <c:if test="${not empty errorMessage}"><p class="flash flash--error">${errorMessage}</p></c:if>
    <div class="panel">
        <form class="org-form" method="post" action="${pageContext.request.contextPath}/employee/create">
            <div class="org-form__grid">
                <section class="org-form__col">
                    <h2 class="org-form__heading">Hồ sơ</h2>
                    <label class="org-form__field">Mã NV
                        <input name="employeeCode" required value="${emp.employeeCode}">
                    </label>
                    <label class="org-form__field">Họ tên
                        <input name="fullName" required value="${emp.fullName}">
                    </label>
                    <label class="org-form__field">Giới tính
                        <select name="gender" required>
                            <option value="1">Nam</option>
                            <option value="2">Nữ</option>
                            <option value="3">Khác</option>
                        </select>
                    </label>
                    <label class="org-form__field">Ngày sinh
                        <input type="date" name="birthDate" value="${emp.birthDate}">
                    </label>
                    <label class="org-form__field">TK ngân hàng
                        <input name="bankAccount" value="${emp.bankAccount}">
                    </label>
                    <label class="org-form__field">Chức vụ
                        <select name="positionId" required>
                            <c:forEach var="p" items="${positions}">
                                <option value="${p.positionId}">${p.positionCode} — ${p.positionName}</option>
                            </c:forEach>
                        </select>
                    </label>
                    <label class="org-form__field">Nhóm LĐ
                        <select name="employmentGroup" required>
                            <option value="1">Văn phòng</option>
                            <option value="2">Nhà máy</option>
                        </select>
                    </label>
                    <label class="org-form__field">Ngày vào
                        <input type="date" name="joiningDate" required value="${emp.joiningDate}">
                    </label>
                </section>

                <section class="org-form__col">
                    <h2 class="org-form__heading">Hợp đồng đầu</h2>
                    <label class="org-form__field">Loại
                        <select name="contractType" required>
                            <option value="1">Thử việc</option>
                            <option value="2">Chính thức</option>
                        </select>
                    </label>
                    <label class="org-form__field">Ngày bắt đầu
                        <input type="date" name="startDate" required value="${con.startDate}">
                    </label>
                    <label class="org-form__field">Ngày kết thúc
                        <input type="date" name="endDate" value="${con.endDate}">
                    </label>
                    <label class="org-form__field">Lương cơ bản
                        <input name="basicSalary" required value="${con.basicSalary}">
                    </label>
                    <label class="org-form__field">Hình thức lương
                        <select name="salaryType" required>
                            <option value="1">Theo tháng</option>
                            <option value="2">Theo giờ</option>
                        </select>
                    </label>
                </section>
            </div>

            <div class="org-form__actions">
                <button class="btn btn-primary" type="submit">Lưu</button>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/employee/list">Hủy</a>
            </div>
        </form>
    </div>
</main>
</body>
</html>
