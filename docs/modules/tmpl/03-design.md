# 03-design.md - {Tên Module}

**Module:** {Module Name}  
**Domain:** `{domain}`  
**Feature(s):** `{feature}` …  
**Version:** 1.0.0 | **Date:** YYYY-MM-DD | **Status:** Draft

---

## 1. URL Mapping

Quy tắc: URL `/{feature}/{action}` (`05-coding-convention`).  
JSP: `/WEB-INF/views/{domain}/{feature}/{feature}-{action}.jsp`  
Package controller: `com.hrm.controller.{domain}.{featureSansHyphen}` (bỏ `-`).

*Chỉ giữ dòng action có trong Specification. Module READ-only / admin / common: xóa CRUD thừa.*

| Method | URL | Controller | Permission / cơ chế | View / Redirect |
| :--- | :--- | :--- | :--- | :--- |
| GET | `/{feature}/list` | `List{Feature}Controller` | `{feature}:READ` | `{feature}-list.jsp` |
| GET | `/{feature}/detail` | `Detail{Feature}Controller` | `{feature}:READ` | `{feature}-detail.jsp` |
| GET | `/{feature}/create` | `Create{Feature}Controller` | `{feature}:CREATE` | `{feature}-create-form.jsp` |
| POST | `/{feature}/create` | `Create{Feature}Controller` | `{feature}:CREATE` | Redirect `/{feature}/list` |
| GET | `/{feature}/edit` | `Edit{Feature}Controller` | `{feature}:UPDATE` | `{feature}-edit-form.jsp` |
| POST | `/{feature}/update` | `Update{Feature}Controller` | `{feature}:UPDATE` | Redirect `/{feature}/list` |
| POST | `/{feature}/delete` | `Delete{Feature}Controller` | `{feature}:DELETE` | Redirect `/{feature}/list` |

*Ví dụ dùng chung (không permission):* `POST /change-password` → whitelist sau login.  
*Ví dụ ADMIN realm:* `/permission-matrix/list` — Filter tách realm, không `position_permission`.

## 2. DTO Fields

### 2.1 Request — `{Feature}FormRequest`
*Dùng chung Create/Update trừ khi Update có field client bắt buộc thêm → `{Feature}UpdateRequest` (xem `05`).*
- field… (type)

### 2.2 Response — `{Feature}Response`
- `{entity}Id` (hoặc tên khớp convention module)
- … fields hiển thị
- `{concept}Status` — nếu có workflow (không nhầm soft-delete)
- `status` — soft-delete nếu cần expose
- `createdAt`

### 2.3 Request đặc thù (nếu có)
- VD: `ChangePasswordForm`, `PermissionMatrixUpdateRequest`, `UpdateUserStatusRequest` — liệt kê field.

## 3. List filters (nếu có)
- VD: `/{feature}/list?page=1&size=20&keyword=…`  
- USER: DataScope áp **trước** COUNT/LIMIT (`05` §3f).
