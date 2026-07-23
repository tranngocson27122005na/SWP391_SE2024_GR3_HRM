# 02-database.md - Common Auth

**Module:** Common Auth (Core)  
**Domain:** `common`  
**Version:** 1.0.0 | **Date:** 2026-07-22 | **Status:** Draft  
**Schema nguồn:** `sql/0.hrmdb.sql` (database `hrmdb`)

---

## 1. Global column rules

Tuân `docs/modules/tmpl/02-database.md` + ADR-0002. Module này **không tạo bảng mới**.

## 2. Table Definitions (tham chiếu Core — dùng khi login / matrix)

| Bảng | Mục đích với common-auth |
| :--- | :--- |
| `sys_user` | Xác thực username / password_hash / status / employee_id |
| `role`, `user_role` | Xác định đúng 1 realm ADMIN \| USER |
| `employee`, `job_position` | USER: positionId, departmentId, data_scope |
| `permission`, `position_permission` | Nguồn cache `PositionPermissionMatrix` (load/reload) |

Không mô tả lại DDL đầy đủ — xem `sql/0.hrmdb.sql`.

**Ghi chú TINYINT:** `sys_user.status`, `job_position.data_scope`, `permission.action` — nghĩa ở enum Java khi implement.

## 3. Permission Seeds

**Không seed permission** cho login / logout / home / change-password / error.

Catalog + `position_permission` do seed toàn cục / module admin & authority quản lý; common-auth chỉ **đọc** để nạp Matrix và home USER.

## 4. Home hub (không bảng menu, không side-bar trên `/home`)

| Realm | Nguồn ô lưới |
| :--- | :--- |
| ADMIN | Ô hệ thống cố định → khu admin |
| USER | Ô theo permission/module từ cache `positionId` |

Layout: grid ô (icon + nhãn); optional ô tìm kiếm phía trên. Màu tự do.
