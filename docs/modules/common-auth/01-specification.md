# 01-specification.md - Common Auth

**Module:** Common Auth (Core)  
**Domain:** `common`  
**Feature(s):** `login`, `logout`, `home`, `change-password`, `error` (+ hạ tầng Filter/Matrix — không phải business resource)  
**Discovery ref:** `docs/discovery/auth/01-login-authorization.md`  
**Version:** 1.1.0 | **Date:** 2026-07-22 | **Status:** Draft  
**ADR:** ADR-0001, ADR-0002

---

## 1. Context

Cổng vào hệ thống: đăng nhập / đăng xuất / phiên, trang chủ theo realm, đổi mật khẩu bản thân, AuthFilter tách ADMIN|USER, trang lỗi 403/404/500. Đây là MVP ưu tiên #1 — mọi module sau phụ thuộc module này.

## 2. Scope

- **In scope**:
  - Đăng nhập, đăng xuất, session (timeout Core).
  - Xây `UserSession` theo realm (ADMIN mỏng / USER đủ ngữ cảnh + dataScope).
  - `AuthFilter`: chưa login → login; sai realm / USER thiếu permission → 403.
  - `PositionPermissionMatrix`: load / hasPermission / reload (USER tra theo `positionId` từ cache).
  - **Home hub `/home`**: màn hình **lưới ô chức năng** (grid). **Không dùng side-bar** trên màn này (hub thay cặp home+sidebar). Bố cục theo mock (ô bo góc, icon + nhãn, có thể có ô tìm kiếm); **màu/icon tự chọn**.
  - Đổi mật khẩu bản thân (mọi người đã login).
  - Trang lỗi 403 / 404 / 500 qua `ErrorController`.
- **Out of scope / Non-goals**:
  - Ma trận UI, list/khóa `sys_user` → module **admin**.
  - Xem/CRUD employee → module **org** (chỉ READ emp trong MVP org).
  - Xem department / job-position UI → **không** làm wave này.
  - Quên mật khẩu / email; tạo TK khi có employee mới; bảng menu; đơn từ.

## 3. Actors & DataScope

| Actor | Realm | DataScope | Ghi chú |
| :--- | :--- | :--- | :--- |
| Anonymous | — | — | Chỉ login (và static) |
| **ADMIN** (đã login) | Hệ thống | N/A | Home admin; đổi MK; không vào URL nghiệp vụ |
| **USER** (đã login) | Nghiệp vụ | Có trên session (`dataScope`) — **không áp** trong FR common-auth | Home theo permission chức danh; đổi MK |

Mọi FR dưới đây **không** gán `position_permission` (dùng chung / hạ tầng).

## 4. Feature List & Permissions

**Không seed permission** cho login, logout, home, change-password, error.

| Feature | Cơ chế kiểm soát |
| :--- | :--- |
| `login` | Public |
| `logout` | Public hoặc đã login |
| `home` | Whitelist sau login (không check permission cụ thể) |
| `change-password` | Whitelist sau login |
| `error` | Qua `<error-page>` / Filter |
| AuthFilter + Matrix | Hạ tầng — áp mọi request (trừ whitelist) |

Chi tiết ánh xạ URL→permission nghiệp vụ: convention `05` + cache Matrix; URL admin vs nghiệp vụ từ Spec **admin** / **org** (Filter enforce realm).

## 5. Functional Requirements

| FR ID | Mô tả | Actor | Permission / cơ chế | Acceptance Criteria |
| :--- | :--- | :--- | :--- | :--- |
| FR-01 | Đăng nhập bằng username/password | Anonymous | Public | Đúng MK + status active + đúng 1 role ADMIN\|USER → tạo session → redirect `/home`. Sai/khóa → message chung tiếng Việt, không tạo session. |
| FR-02 | Xây UserSession theo realm | Hệ thống (sau FR-01) | — | ADMIN: userId, username, roles={ADMIN}; không employee/position/dataScope. USER: thêm employeeId, positionId, departmentId, dataScope từ job_position. Đúng một role. |
| FR-03 | Đăng xuất | Đã login | Logout | Xóa session; request sau phải login lại. |
| FR-04 | AuthFilter — chưa đăng nhập | Anonymous | Redirect `/login` | Mọi URL ngoài whitelist public → `/login`. |
| FR-05 | AuthFilter — tách realm | ADMIN / USER | 403 nếu sai realm | ADMIN không vào URL nghiệp vụ; USER không vào URL hệ thống (admin). Đã login → **không** redirect `/login`. |
| FR-06 | AuthFilter — permission USER | USER | Matrix + positionId | Thiếu permission cho URL → 403. Permission từ cache theo `positionId` (sau reload ma trận có hiệu lực ngay). |
| FR-07 | Trang chủ hub `/home` (lưới ô) | Đã login | Whitelist | **Không side-bar.** Lưới ô (grid). ADMIN: **2 ô** Tài khoản + Ma trận → 2 URL/trang riêng. USER: ô theo permission. Không bảng menu. Bố cục theo mock; màu tự do. Ô chưa mở MVP: ẩn hoặc không click được. |
| FR-08 | Đổi mật khẩu bản thân | Đã login (ADMIN\|USER) | Whitelist | Nhập MK hiện tại đúng + MK mới hợp lệ → hash BCrypt lưu; sai → errorMessage tiếng Việt trên form. |
| FR-09 | Trang lỗi 403/404/500 | — | ErrorController | 403 khi FR-05/06; 404/500 theo Core (`04` §7). |

### FR-07b — Ô hub MVP (nhãn ý; map URL)

| Ô (nhãn ý) | Ai thấy | Đích |
| :--- | :--- | :--- |
| Tài khoản | ADMIN | `/sys-user/list` (trang list riêng — module **admin**) |
| Ma trận phân quyền | ADMIN | `/permission-matrix/list` (trang ma trận riêng — module **admin**) |
| Nhân viên | USER có `employee:READ` | `/employee/list` (module **org**) |
| Chấm công / Hợp đồng / Tiền lương / … | USER khi có permission + Spec | Wave sau — ẩn hoặc disable |

*ADMIN có **2 ô riêng** trên hub (không gộp một ô “Hệ thống”). Mỗi ô → một trang độc lập.*

## 6. Business Rules (Service)

- **BR-01**: Password chỉ lưu BCrypt (salt rounds = 10). Không lưu plain text.
- **BR-02**: Thông báo đăng nhập thất bại **chung** (không phân biệt sai user / sai MK / user khóa).
- **BR-03**: `sys_user` phải có đúng **một** role `ADMIN` hoặc `USER`. Không đúng → không cho login (ValidationException / BusinessException).
- **BR-04**: USER login bắt buộc `employee_id` NOT NULL và truy ra được `job_position` (positionId, departmentId, dataScope). Thiếu → không cho login.
- **BR-05**: ADMIN login: `employee_id` phải NULL.
- **BR-06**: Đổi MK: MK hiện tại phải khớp; MK mới khác MK cũ; độ dài tối thiểu theo validation Controller.
- **BR-07**: Sau khi module admin cập nhật ma trận → `PositionPermissionMatrix.reload()`; request/home sau dùng cache mới (không bắt re-login).

## 7. Validation Rules (Controller)

### 7.1 Login
| Field | Rule | Error Message (Tiếng Việt) |
| :--- | :--- | :--- |
| `username` | Required, max 50 | "Vui lòng nhập tên đăng nhập" |
| `password` | Required | "Vui lòng nhập mật khẩu" |

### 7.2 Đổi mật khẩu
| Field | Rule | Error Message (Tiếng Việt) |
| :--- | :--- | :--- |
| `currentPassword` | Required | "Vui lòng nhập mật khẩu hiện tại" |
| `newPassword` | Required, min 6 | "Mật khẩu mới phải có ít nhất 6 ký tự" |
| `confirmPassword` | Required, khớp newPassword | "Xác nhận mật khẩu không khớp" |

## 8. Dependencies

- Schema: `sys_user`, `role`, `user_role`, `employee`, `job_position`, `permission`, `position_permission` (`sql/0.hrmdb.sql`).
- Init: `04-architecture` §4–§7, `05` §3b/3e, `07-glossary`.
- Discovery: `docs/discovery/auth/01-login-authorization.md`.
- Module sau: **admin**, **org** (`employee:READ`).
- Home hub: discovery §5; mock bố cục lưới (màu tự do).
