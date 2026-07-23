# 02-database.md - Admin (Hệ thống)

**Module:** Admin (System Realm)  
**Domain:** `admin`  
**Version:** 1.0.0 | **Date:** 2026-07-22 | **Status:** Draft  
**Schema nguồn:** `sql/0.hrmdb.sql` (database `hrmdb`)

---

## 1. Global column rules

Tuân template + ADR-0002. Module **không tạo bảng mới**.

## 2. Table Definitions (tham chiếu)

| Bảng | Dùng cho |
| :--- | :--- |
| `sys_user` | List / status / password_hash |
| `user_role`, `role` | Hiển thị đúng 1 role ADMIN\|USER |
| `employee` | Join tên/mã NV khi user có `employee_id` |
| `job_position`, `department` | Hàng ma trận |
| `permission` | Cột ma trận (đọc seed; không CRUD) |
| `position_permission` | Gán/bỏ; `granted_by`, `granted_at` |

Không dùng `role_permission`.

## 3. Permission Seeds

**Không seed** permission kiểu `sys-user:*` / `permission-matrix:*` cho `position_permission` trong MVP.

Catalog nghiệp vụ (`employee:READ`, …) giữ ở seed toàn cục / authority — admin chỉ **đọc** để vẽ ma trận và ghi `position_permission`.

## 4. Home hub — 2 ô ADMIN (trang riêng)

Từ `/home` (common-auth, **không side-bar**), ADMIN thấy **hai ô**:

| Label (ý) | URL | Trang |
| :--- | :--- | :--- |
| Tài khoản | `/sys-user/list` | List tài khoản (độc lập) |
| Ma trận phân quyền | `/permission-matrix/list` | Ma trận chức danh ↔ permission (độc lập) |

Không gộp một ô “Hệ thống”. Không bảng `menu`.
