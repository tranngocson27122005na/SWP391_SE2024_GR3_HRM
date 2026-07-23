# 01-specification.md - Admin (Hệ thống)

**Module:** Admin (System Realm)  
**Domain:** `admin`  
**Feature(s):** `sys-user`, `permission-matrix`  
**Discovery ref:** `docs/discovery/auth/01-login-authorization.md`  
**Depends on:** `docs/modules/common-auth/` (Filter realm, Matrix.reload, session)  
**Version:** 1.0.0 | **Date:** 2026-07-22 | **Status:** Draft  
**ADR:** ADR-0001, ADR-0002

---

## 1. Context

Module dành **chỉ ADMIN**: quản lý tài khoản đăng nhập ở mức vận hành tối thiểu (xem danh sách, khóa/mở, đổi mật khẩu giúp user) và cấu hình ma trận **chức danh ↔ permission** trên catalog đã seed. Không tham gia nghiệp vụ doanh nghiệp (employee/công/lương…).

## 2. Scope

- **In scope**:
  - `sys-user`: **list**, **khóa / mở**, **đổi mật khẩu giúp user**, **bù tạo TK** cho emp chưa có `sys_user` (ADR-0004).
  - **BR-ADM-SYNC-01**: ADMIN kiểm tra **mỗi ngày làm việc** hàng đợi onboard (emp chưa TK) + offboard (user ACTIVE + emp không HĐ ACTIVE).
  - `permission-matrix`: xem / gán / bỏ.
- **Out of scope / Non-goals**:
  - Auto/trigger/notification provision TK.
  - Tạo TK từ org; CRUD catalog permission; CRUD dept/job-position/employee UI.

## 3. Actors & DataScope

| Actor | Realm | DataScope | Ghi chú |
| :--- | :--- | :--- | :--- |
| **ADMIN** | Hệ thống | N/A | Toàn bộ FR module này |
| **USER** (mọi chức danh) | Nghiệp vụ | — | **Không** truy cập — Filter 403 |

Không áp DataScope nghiệp vụ.

## 4. Feature List & Permissions

Thao tác **ADMIN-only** — enforce bằng **realm** trong AuthFilter (common-auth), **không** seed `position_permission` cho các URL admin.

| Feature | Thao tác MVP | Cơ chế |
| :--- | :--- | :--- |
| `sys-user` | list, update-status, reset-password, **provision (bù TK)** | ADMIN realm |
| `permission-matrix` | list (xem), update (gán/bỏ) | ADMIN realm |

*Không dùng permission `sys-user:READ` / `permission-matrix:UPDATE` trên ma trận chức danh trong MVP — tránh USER “được gán” nhầm quyền admin.*

## 5. Functional Requirements

| FR ID | Mô tả | Actor | Cơ chế | Acceptance Criteria |
| :--- | :--- | :--- | :--- | :--- |
| FR-01 | Xem danh sách tài khoản | ADMIN | Realm | Thấy username, status, role (ADMIN\|USER), employee liên kết (nếu có); phân trang; lọc status/keyword. Thao tác khóa/mở và đổi MK giúp thực hiện từ list (không trang detail riêng). |
| FR-02 | Khóa / mở tài khoản | ADMIN | Realm | Cập nhật `sys_user.status` ACTIVE↔INACTIVE. Không xóa cứng. Không khóa chính mình nếu là ADMIN đang login duy nhất đang mở (BR-U03). |
| FR-03 | Đổi mật khẩu giúp user | ADMIN | Realm | ADMIN nhập MK mới (+ confirm); BCrypt lưu; không cần MK cũ của user đích. |
| FR-04 | Xem ma trận phân quyền | ADMIN | Realm | Lưới/bảng: hàng = `job_position` (code/name/dept), cột = permission seed; ô checked = `position_permission`. |
| FR-05 | Cập nhật ma trận (gán/bỏ) | ADMIN | Realm | Lưu cặp position↔permission; chỉ catalog seed; 1 transaction; `PositionPermissionMatrix.reload()`. |
| FR-07 | Bù tạo TK cho emp chưa có user | ADMIN | Realm | List emp chưa gắn sys_user; tạo USER, username=mã NV, MK mặc định, gắn employee_id. |
| FR-08 | Kiểm tra định kỳ | ADMIN | BR-ADM-SYNC-01 | Mỗi ngày làm việc xử lý hàng đợi onboard + offboard (discovery admin). |

## 6. Business Rules (Service)

### 6.1 `sys-user`
- **BR-U01**: Chỉ ADMIN gọi được Service (Controller/Filter đã chặn; Service có thể double-check role).
- **BR-U02**: Soft lock = `status` INACTIVE; không DELETE vật lý.
- **BR-U03**: Không cho ADMIN khóa chính tài khoản đang đăng nhập nếu sau thao tác không còn ADMIN active nào khác (tránh khóa hệ thống). *Nếu chỉ có 1 ADMIN active = chính mình → từ chối khóa.*
- **BR-U04**: Đổi MK giúp: MK mới min length theo validation; hash BCrypt salt 10; không lưu plain text.
- **BR-U05**: Không đổi `username` / `role` tùy tiện; provision chỉ gắn emp chưa có user.
- **BR-U06 (ADR-0004)**: Không auto từ org; kiểm tra mỗi ngày làm việc — BR-ADM-SYNC-01.

### 6.2 `permission-matrix`
- **BR-P01**: Chỉ thao tác trên `permission` đã tồn tại (seed). Không INSERT/UPDATE/DELETE bảng `permission`.
- **BR-P02**: Chỉ gán cho `job_position` đang ACTIVE (status ACTIVE). Position inactive → không hiện hoặc không cho sửa (chốt: không cho cập nhật).
- **BR-P03**: `granted_by` = `userId` ADMIN hiện tại; `granted_at` = now khi insert.
- **BR-P04**: Update = thay thế tập permission của từng position được submit (xóa pair bỏ check + insert pair mới) trong **một** transaction — hoặc diff rõ ràng; không để DB nửa vời.
- **BR-P05**: Sau commit thành công → `PositionPermissionMatrix.reload()`.
- **BR-P06**: Không ghi `role_permission`.

## 7. Validation Rules (Controller)

### 7.1 Lock / unlock
| Field | Rule | Error Message (Tiếng Việt) |
| :--- | :--- | :--- |
| `userId` / `id` | Required, số hợp lệ | "Tài khoản không hợp lệ" |
| `status` | Required, chỉ code ACTIVE/INACTIVE | "Trạng thái không hợp lệ" |

### 7.2 Đổi MK giúp user
| Field | Rule | Error Message (Tiếng Việt) |
| :--- | :--- | :--- |
| `userId` / `id` | Required | "Tài khoản không hợp lệ" |
| `newPassword` | Required, min 6 | "Mật khẩu mới phải có ít nhất 6 ký tự" |
| `confirmPassword` | Khớp newPassword | "Xác nhận mật khẩu không khớp" |

### 7.3 Cập nhật ma trận
| Field | Rule | Error Message (Tiếng Việt) |
| :--- | :--- | :--- |
| `assignments` (hoặc tương đương) | Định dạng đúng; positionId/permissionId tồn tại | "Dữ liệu ma trận không hợp lệ" |

## 8. Dependencies

- Common-auth: AuthFilter realm, SessionManager, PositionPermissionMatrix, Error 403.
- Bảng: `sys_user`, `role`, `user_role`, `employee` (join hiển thị), `job_position`, `department` (hiển thị), `permission`, `position_permission`.
- Seed catalog + ma trận khởi tạo: `sql/0.seed_data.sql` (ADMIN có thể chỉnh gán sau).
- Module sau: **org** (READ `employee` theo DataScope).
