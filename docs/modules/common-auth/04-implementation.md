# 04-implementation.md - Common Auth

**Module:** Common Auth (Core)  
**Domain:** `common`  
**Version:** 1.0.0 | **Date:** 2026-07-22 | **Status:** Draft

---

## 1. Package Structure
- Controller: `com.hrm.controller.common` — `LoginController`, `LogoutController`, `HomeController`, `ChangePasswordController`, `ErrorController`
- Service: `com.hrm.service.common` — `AuthService`, `SecurityService` (BCrypt)
- Security: `com.hrm.infrastructure.security` — `AuthFilter`, `SessionManager`, `PositionPermissionMatrix`, `DataScope` / `DataScopeResolver` (resolver dùng module sau; Matrix dùng tại Filter/home)
- Session DTO: `com.hrm.dto.session.UserSession`
- Request DTO: `com.hrm.dto.request.LoginForm`, `ChangePasswordForm` (hoặc tên tương đương)
- Enum: `ActiveStatus` (utility); `PermissionAction` / data_scope enum khi map TINYINT (ADR-0002)
- Views: `/WEB-INF/views/common/*.jsp` + `fragment/side-bar.jsp`, `top-nav.jsp`

## 2. Method Signatures

```
AuthService
  UserSession login(LoginForm form)
  void changePassword(UserSession user, ChangePasswordForm form)

SecurityService
  String hash(String plain)
  boolean matches(String plain, String hash)

SessionManager
  UserSession getUserSession(HttpServletRequest req)
  void setUserSession(HttpServletRequest req, UserSession user)
  void removeUserSession(HttpServletRequest req)
  boolean isLoggedIn(HttpServletRequest req)

PositionPermissionMatrix
  void load()
  void reload()
  boolean hasPermission(Long positionId, String urlPath)   // hoặc API tương đương: permissions(positionId) + urlNeeds
  Set<String> permissionsOf(Long positionId)               // cho home/sidebar USER

AuthFilter
  // @WebFilter("/*") — FR-04..06

HomeController
  // doGet: setAttribute modules/links theo realm → home.jsp

ErrorController
  // forward error-403/404/500.jsp theo status code
```

*Đổi tên method Matrix cho khớp code hiện có khi refactor `RolePermissionMatrix` → `PositionPermissionMatrix` — giữ hành vi FR.*

## 3. Implementation Notes

### 3.1 AuthService.login
1. Trim username; load `sys_user` by username.
2. Nếu null hoặc status ≠ ACTIVE → ValidationException message chung.
3. BCrypt matches; sai → message chung.
4. Load đúng 1 role name; không phải ADMIN|USER duy nhất → BusinessException / ValidationException.
5. Nếu ADMIN: employee_id phải null; build session mỏng.
6. Nếu USER: load employee → job_position; set employeeId, positionId, departmentId, dataScope; thiếu → không login.
7. `SessionManager.setUserSession` → caller redirect `/home`.

### 3.2 AuthFilter
1. Public path → chain.
2. Session null → redirect `/login`.
3. Login-only path (`/home`, `/change-password`) → chain.
4. Phân nhánh realm theo bảng path trong `03-design.md` (ADMIN: `/sys-user/**`, `/permission-matrix/**`; USER nghiệp vụ: `/employee/**` MVP).
5. USER: `PositionPermissionMatrix.hasPermission(positionId, path)` — false → 403 (`sendError` hoặc ErrorController; **web.xml** map 403 → error-403, **không** login).
6. init(): `PositionPermissionMatrix.load()`.

### 3.3 HomeController
- **Không** render side-bar trên `/home`.
- Build `List<HomeTile>` (hoặc tương đương): `code`, `label`, `iconKey`, `url`, `visible`.
- ADMIN: **hai ô** — Tài khoản → `/sys-user/list`; Ma trận phân quyền → `/permission-matrix/list` (hai trang riêng, không gộp).
- USER: tile theo permission (ít nhất `employee:READ` → Nhân viên → `/employee/list` khi có module org).
- `home.jsp`: lưới ô; optional search filter client-side trên label ô.

### 3.4 changePassword
- Verify current bằng BCrypt; validate new/confirm; update `password_hash` trong 1 UoW.

### 3.5 web.xml
- `session-timeout` 30.
- `<error-page>` 403/404/500 → ErrorController hoặc JSP lỗi common.

### 3.6 Refactor bắt buộc so với code cũ
- `RolePermissionMatrix` / check theo **roles** → Matrix theo **position_permission** + positionId.
- Sidebar bỏ hard-code MANAGER/DIRECTOR/EMPLOYEE.
- 403 không còn trỏ `login.jsp`.

## 4. Checklist trước khi AI code
- [ ] Đủ class §2 khớp Spec FR-01..09 và Design URL
- [ ] Không seed permission cho feature common
- [ ] Một sys_user một role; ADMIN/USER session đúng § Spec
- [ ] 403 lập trình; reload Matrix không bắt re-login
- [ ] Không thêm quên MK / admin matrix UI / CRUD employee vào module này
