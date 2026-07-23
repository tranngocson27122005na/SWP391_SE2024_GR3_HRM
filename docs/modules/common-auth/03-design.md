# 03-design.md - Common Auth

**Module:** Common Auth (Core)  
**Domain:** `common`  
**Feature(s):** `login`, `logout`, `home`, `change-password`, `error`  
**Version:** 1.0.0 | **Date:** 2026-07-22 | **Status:** Draft

---

## 1. URL Mapping

JSP dưới `/WEB-INF/views/common/`. Controller: `com.hrm.controller.common`.

| Method | URL | Controller | Cơ chế | View / Redirect |
| :--- | :--- | :--- | :--- | :--- |
| GET | `/login` | `LoginController` | Public | `login.jsp` |
| POST | `/login` | `LoginController` | Public | Thành công → redirect `/home`; lỗi → forward `login.jsp` + `errorMessage` |
| GET/POST | `/logout` | `LogoutController` | Public / session | Xóa session → redirect `/login` |
| GET | `/home` | `HomeController` | Login whitelist | `home.jsp` — **hub lưới ô, không include side-bar** |
| GET | `/change-password` | `ChangePasswordController` | Login whitelist | `change-password.jsp` |
| POST | `/change-password` | `ChangePasswordController` | Login whitelist | OK → redirect `/home` (hoặc message thành công); lỗi → forward form |
| — | (error-page 403/404/500) | `ErrorController` | `web.xml` | `error-403.jsp` / `error-404.jsp` / `error-500.jsp` |

**Whitelist & phân realm cho AuthFilter (SSoT MVP)**

| Nhóm | Path / prefix | Hành vi |
| :--- | :--- | :--- |
| Public | `/`, `/index.jsp`, `/login`, `/logout`, `/static/**` | Không cần session |
| Login-only | `/home`, `/change-password` | Cần session; **không** check permission; **không** phân realm |
| Error | `/error/**` hoặc path ErrorController + forward JSP lỗi | Không đòi permission |
| **Realm ADMIN** | `/sys-user/**`, `/permission-matrix/**` | Chỉ session ADMIN; USER → 403 |
| **Realm USER (nghiệp vụ)** | `/employee/**` (org MVP) | Chỉ session USER + `PositionPermissionMatrix` theo `positionId`; ADMIN → 403 |
| Khác (chưa map) | Mọi path còn lại sau whitelist | Coi như cần login; nếu không thuộc ADMIN/USER prefix → 403 (an toàn) |

*Wave sau thêm prefix nghiệp vụ (`/attendance/**`, `/contract/**`, …) vào realm USER khi có Spec.*

URL nghiệp vụ vs hệ thống: bảng trên là nguồn duy nhất cho Filter common-auth (`04` §4.1b).

## 2. DTO Fields

### 2.1 `LoginForm` (request)
- `username` (String)
- `password` (String)

### 2.2 `ChangePasswordForm` (request)
- `currentPassword` (String)
- `newPassword` (String)
- `confirmPassword` (String)

### 2.3 `UserSession` (session — khớp `07-glossary`)
- `userId`, `username`, `roles` (Set — đúng một ADMIN\|USER)
- USER: `employeeId`, `positionId`, `departmentId`, `dataScope` (Integer code)
- Permissions nghiệp vụ: **không bắt buộc snapshot cứng**; Filter/home đọc Matrix theo `positionId`

## 3. List filters

N/A (không danh sách phân trang trong module này).

## 4. Layout `/home` (hub)

- **Không** `jsp:include` `side-bar.jsp` trên `home.jsp`.
- Cấu trúc gợi ý: (optional) thanh tìm kiếm → lưới ô `div`/`a` (icon + label).
- Mỗi ô: `href` tới URL module tương ứng; ADMIN/USER khác tập ô.
- Fragment `side-bar.jsp` có thể dùng cho **màn list/detail module khác** (sau) — không bắt buộc giống hub.
- Tham chiếu bố cục: mock hub (grid); màu/icon không khóa theo mock.
