# 04-implementation.md - Admin (Hệ thống)

**Module:** Admin (System Realm)  
**Domain:** `admin`  
**Version:** 1.0.0 | **Date:** 2026-07-22 | **Status:** Draft

---

## 1. Package Structure
- Controller: `com.hrm.controller.admin.sysuser`, `com.hrm.controller.admin.permissionmatrix`
- Service: `com.hrm.service.admin` — `SysUserAdminService`, `PermissionMatrixService` (hoặc tên tương đương; tách khỏi service nghiệp vụ authority)
- Mapper: `SysUserMapper`, `UserRoleMapper`, `RoleMapper`, `EmployeeMapper`, `JobPositionMapper`, `DepartmentMapper`, `PermissionMapper`, `PositionPermissionMapper`
- DTO: `dto.response.SysUserResponse`, `PermissionMatrixResponse`, `dto.request.ResetSysUserPasswordRequest`, `PermissionMatrixUpdateRequest`, …
- Security: gọi `PositionPermissionMatrix.reload()` sau update ma trận
- Views: `/WEB-INF/views/admin/sys-user/…`, `…/permission-matrix/…`

## 2. Method Signatures

```
SysUserAdminService
  List<SysUserResponse> getList(UserSession admin, Paging paging, Integer statusFilter, String keyword)
  void updateStatus(UserSession admin, Long userId, int statusCode)
  void resetPassword(UserSession admin, ResetSysUserPasswordRequest req)

PermissionMatrixService
  PermissionMatrixResponse getMatrix(UserSession admin)
  void updateMatrix(UserSession admin, PermissionMatrixUpdateRequest req)
```

*Mọi method: nếu `admin` không phải ADMIN → UnauthorizedException (phòng thủ).*

## 3. Implementation Notes

### 3.1 SysUserAdminService
- List: JOIN user_role/role; LEFT JOIN employee; mặc định có thể lọc status ACTIVE hoặc cho phép filter.
- `updateStatus`: load user; BR-U03 nếu self-lock; set status code ActiveStatus; 1 UoW.
- `resetPassword`: validate confirm; `SecurityService.hash`; update `password_hash` only.

### 3.2 PermissionMatrixService.getMatrix
- Load job_position ACTIVE (+ department name), permission catalog, all position_permission rows → view model.

### 3.3 PermissionMatrixService.updateMatrix
1. Parse assignments → Set of (positionId, permissionId); validate IDs tồn tại; position ACTIVE; permission trong catalog.
2. Trong 1 SqlExecutor transaction:
   - Cách đề xuất: với mỗi `positionId` xuất hiện trong form submit, xóa hết `position_permission` của position đó rồi insert các permission được check; **hoặc** diff toàn cục nếu UI submit full matrix.
   - Set `granted_by` = admin.userId.
3. Commit → `PositionPermissionMatrix.reload()`.
4. Không đụng `role_permission` / bảng `permission` (DML catalog).

### 3.4 Controllers
- Không try-catch nghiệp vụ toàn cục; lỗi validation form → forward + `errorMessage`.
- URL chỉ ADMIN — dựa Filter common-auth; có thể assert session role trong Controller.

### 3.5 Refactor so với code cũ
- UI/matrix cũ theo **role** → theo **position**.
- `PermissionMatrixUpdateRequest` bỏ `roleId_permissionId`.
- Service matrix đọc/ghi `position_permission`.

## 4. Checklist trước khi AI code
- [ ] URL/DTO khớp 03; chỉ ADMIN realm; **không** `/sys-user/detail`
- [ ] Không tạo/xóa user; không CRUD catalog permission
- [ ] BR-U03 self-lock; BCrypt reset password
- [ ] updateMatrix + reload Matrix
- [ ] Không seed position_permission cho feature admin
- [ ] JSP/sidebar ADMIN trỏ `/sys-user/list`, `/permission-matrix/list`
