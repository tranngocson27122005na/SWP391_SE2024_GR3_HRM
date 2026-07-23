# 03-design.md - Admin (Hệ thống)

**Module:** Admin (System Realm)  
**Domain:** `admin`  
**Feature(s):** `sys-user`, `permission-matrix`  
**Version:** 1.0.0 | **Date:** 2026-07-22 | **Status:** Draft

---

## 1. URL Mapping

JSP: `/WEB-INF/views/admin/{feature}/…`  
Controller package: `com.hrm.controller.admin.sysuser` / `…permissionmatrix` (bỏ `-`).

Toàn bộ URL dưới đây = **realm ADMIN** (AuthFilter). USER → 403.

### 1.1 `sys-user` (chỉ list — không trang detail riêng)

| Method | URL | Controller | View / Redirect |
| :--- | :--- | :--- | :--- |
| GET | `/sys-user/list` | `ListSysUserController` | `sys-user-list.jsp` (hiển thị đủ cột; thao tác khóa/mở + link đổi MK trên từng dòng) |
| POST | `/sys-user/update-status` | `UpdateSysUserStatusController` | Redirect `/sys-user/list` |
| GET | `/sys-user/reset-password` | `ResetSysUserPasswordController` | `sys-user-reset-password.jsp` |
| POST | `/sys-user/reset-password` | `ResetSysUserPasswordController` | Redirect `/sys-user/list` hoặc forward kèm lỗi |

*Không có `/sys-user/detail` — gộp vào list để không phình scope.*

### 1.2 `permission-matrix`

| Method | URL | Controller | View / Redirect |
| :--- | :--- | :--- | :--- |
| GET | `/permission-matrix/list` | `ListPermissionMatrixController` | `permission-matrix-list.jsp` |
| POST | `/permission-matrix/update` | `UpdatePermissionMatrixController` | Redirect `/permission-matrix/list` |

## 2. DTO Fields

### 2.1 `SysUserResponse`
- `userId`, `username`, `status`, `roleName` (ADMIN\|USER)
- `employeeId` (nullable), `employeeCode`, `employeeName` (nullable)
- `createdAt`

### 2.2 `UpdateSysUserStatusRequest` (hoặc param)
- `userId` (Long)
- `status` (Integer/Byte — code ActiveStatus)

### 2.3 `ResetSysUserPasswordRequest`
- `userId` (Long)
- `newPassword` (String)
- `confirmPassword` (String)

### 2.4 `PermissionMatrixResponse`
- Danh sách positions: `positionId`, `positionCode`, `positionName`, `departmentName`, `dataScope`
- Danh sách permissions: `permissionId`, `permissionName`, `resource`, `action` (code hoặc label)
- Tập đang gán: pairs `(positionId, permissionId)` hoặc map tương đương

### 2.5 `PermissionMatrixUpdateRequest`
- `assignments`: danh sách pair được check — VD `List<{ positionId, permissionId }>` hoặc token `{positionId}_{permissionId}` (khớp UI checkbox)
- **Không** còn format `roleId_permissionId` (mô hình cũ)

## 3. List filters
- `/sys-user/list?page=1&size=20&status=1&keyword=…` (keyword theo username; có thể mở rộng employee name)
- Không DataScope USER (toàn bộ tài khoản cho ADMIN)
