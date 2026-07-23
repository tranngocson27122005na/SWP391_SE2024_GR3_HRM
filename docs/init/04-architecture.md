# 04-architecture.md

**System Architecture**  
**HRMS for M&T Company**  
**Version:** 3.2.0  
**Date:** 2026-07-22  
**Status:** Draft for Review  
**ADR liên quan:** `docs/decisions/ADR-0001-position-centric-authorization.md`, `docs/decisions/ADR-0002-tinyint-code-enums.md`

---

## 1. Architectural Style

Dự án áp dụng **MVC mở rộng kết hợp DDD Lite**, hiện thực bằng **core Servlet/JSP thuần** (không framework):

- **MVC**: mỗi Controller là một `HttpServlet` (`@WebServlet`), Service chứa business logic, JSP + JSTL đảm nhận presentation.
- **DDD Lite**: Tổ chức code theo domain và bounded context, tập trung vào ngôn ngữ chung (Ubiquitous Language) từ glossary.

> Tài liệu này định nghĩa **nền tảng/khái niệm** để các module tham chiếu. Chi tiết triển khai (flow từng bước, API cụ thể, câu SQL) do tài liệu module mô tả khi triển khai.

---

## 2. Layered Architecture

Presentation (JSP + Controller)
↓
Service Layer (Business Logic + Transaction)
↓
Persistence Layer (MyBatis Mapper)
↓
Database (MySQL)

**Mô tả chi tiết các layer:**

- **Controller**: Chỉ định tuyến, gọi Service, trả về view hoặc redirect. Không chứa business logic.
- **Service**: Chứa toàn bộ business rule, transaction boundary, gọi Mapper. Là trung tâm của DDD Lite.
- **Persistence**: MyBatis Mapper interfaces + XML mappings. Entities là anemic.
- **Infrastructure**: Các thành phần ngang (Security Filter, Exception Handler, Session Manager, SqlExecutor).

---

## 3. Core Database Foundation

**Quy tắc đặt tên khóa** (khớp schema thực tế `sql/0.hrmdb.sql`, database `hrmdb`):
- **PK**: `{entity}_id` INT AUTO_INCREMENT (VD: `user_id`, `department_id`, `position_id`, `employee_id`).
- **FK nghiệp vụ**: cùng tên cột với PK bảng cha (VD: `employee.position_id` → `job_position.position_id`).
- **FK audit user**: hậu tố `_by` hoặc `updated_by` / `imported_by` / `granted_by` → `sys_user.user_id`.

**TINYINT code columns (ADR-0002):** Mọi cột status / action / type / scope lưu `TINYINT`. Semantics nằm ở **Java enum** (`getCode()` / `fromCode`). **Không** dùng `CHECK (... IN (...))` liệt kê giá trị trên DB — Service validate.

**Soft Delete**: Cột `status` TINYINT (0 = INACTIVE, 1 = ACTIVE) ↔ enum `ActiveStatus`. Không xóa cứng. Mặc định `SELECT` chỉ lấy `status = 1`.

**Workflow status**: Chuỗi trạng thái nghiệp vụ dùng cột `{concept}_status` (TINYINT + enum riêng) — **không** dùng tên `status`. Xem `07-glossary.md` §3.1.

### 3.0 Chuỗi quan hệ gốc (Identity & Org)

```
department
  └── job_position              (phụ thuộc dept; mỗi dept có tập job_pos riêng)
        ├──◀── (N:N) ──▶ permission     qua position_permission
        └── employee
              └── (1:1) sys_user
                        └──▶ role       qua user_role  (ADMIN | USER)
```

### 3.1 department
| Column | Type | Description |
|--------|------|-------------|
| department_id | INT PK AUTO_INCREMENT | Khóa chính |
| department_code | VARCHAR(20) UNIQUE NOT NULL | Mã phòng ban |
| department_name | VARCHAR(100) NOT NULL | Tên phòng ban |
| status | TINYINT DEFAULT 1 | Soft-delete |
| created_at | DATETIME | Ngày tạo |

### 3.2 job_position
| Column | Type | Description |
|--------|------|-------------|
| position_id | INT PK AUTO_INCREMENT | Khóa chính |
| position_code | VARCHAR(20) UNIQUE NOT NULL | Mã chức danh |
| position_name | VARCHAR(100) NOT NULL | Tên chức danh |
| department_id | INT FK NOT NULL | Thuộc phòng ban |
| data_scope | TINYINT NOT NULL DEFAULT 1 | 1=SELF, 2=DEPARTMENT, 3=ALL |
| status | TINYINT DEFAULT 1 | Soft-delete |
| updated_by | INT NULL | FK sys_user — audit khi ADMIN sửa data_scope |
| updated_at | DATETIME NULL | |
| created_at | DATETIME | |

### 3.3 sys_user
| Column | Type | Description |
|--------|------|-------------|
| user_id | INT PK AUTO_INCREMENT | Khóa chính |
| username | VARCHAR(50) UNIQUE NOT NULL | Tên đăng nhập |
| password_hash | VARCHAR(255) NOT NULL | BCrypt |
| employee_id | INT UNIQUE NULL | NULL = tài khoản hệ thống (ADMIN); NOT NULL = tài khoản nhân sự (1-1 với employee) |
| status | TINYINT DEFAULT 1 | 1=active, 0=inactive (khóa) |
| created_at | DATETIME | |

### 3.4 role
| Column | Type | Description |
|--------|------|-------------|
| role_id | INT PK AUTO_INCREMENT | Khóa chính |
| role_name | VARCHAR(50) UNIQUE NOT NULL | Chỉ `ADMIN` hoặc `USER` |

### 3.5 user_role
| Column | Type | Description |
|--------|------|-------------|
| user_id | INT FK | → sys_user |
| role_id | INT FK | → role |
| PK | (user_id, role_id) | |

### 3.6 permission
| Column | Type | Description |
|--------|------|-------------|
| permission_id | INT PK AUTO_INCREMENT | Khóa chính |
| permission_name | VARCHAR(100) UNIQUE NOT NULL | Format `{resource}:{ACTION}` (VD: `employee:READ`) — chuỗi đọc-được |
| resource | VARCHAR(50) NOT NULL | Business resource |
| action | TINYINT NOT NULL | Code số — nghĩa do enum Java (`PermissionAction`); xem ADR-0002 |
| description | VARCHAR(255) | |

### 3.7 position_permission (ma trận phân quyền nghiệp vụ)
| Column | Type | Description |
|--------|------|-------------|
| position_id | INT FK | → job_position |
| permission_id | INT FK | → permission |
| granted_by | INT FK NOT NULL | ADMIN đã cấp |
| granted_at | DATETIME | |
| PK | (position_id, permission_id) | |

> **Đây là nguồn chân lý cho quyền chức năng của USER.** ADMIN cấu hình qua UI. Không ảnh hưởng DataScope.

### 3.8 role_permission
Bảng có thể tồn tại trong schema lịch sử. **MVP: không dùng để gán quyền nghiệp vụ.** Quyền nghiệp vụ chỉ qua `position_permission`. Quyền ADMIN enforce bằng realm trong Filter + module admin (không dựa ma trận nghiệp vụ).

### 3.9 employee (thuộc domain nhân sự, tham chiếu Core)
| Column | Type | Description |
|--------|------|-------------|
| employee_id | INT PK | Hồ sơ nhân sự đang làm tại công ty |
| position_id | INT FK NOT NULL | → job_position |
| employment_group | TINYINT NOT NULL | 1=OFFICE, 2=FACTORY |
| … | | Xem module employee |

> **Không có bảng `menu` trong Core.** Lưới chức năng `/home` và sidebar được **backend suy ra** từ tập permission của user (xem §4.1, §5.4) — không lưu cây menu trong DB.

---

## 4. Authentication & Session

### 4.1 Login Flow
1. User submit username/password qua `LoginController`.
2. Service kiểm tra username + BCrypt.
3. Nếu thành công, tạo `UserSession`:
   - `userId`, `username`, `roles` (đúng một trong: `ADMIN` hoặc `USER` — không kết hợp)
   - Nếu **USER**: `employeeId`, `positionId`, `departmentId`, `dataScope`, `permissions` (từ `position_permission`)
   - Nếu **ADMIN**: chỉ `userId`/`username`/`roles={ADMIN}`; **không** có employee/position/dataScope/permissions nghiệp vụ
4. `SessionManager.setUserSession(...)`.
5. Redirect `/home` (nội dung home **khác nhau** theo realm — §4.1b).

### 4.1b Hai realm: Hệ thống (ADMIN) vs Nghiệp vụ (USER) — không đan xen

| | ADMIN (hệ thống) | USER (nghiệp vụ doanh nghiệp) |
|--|------------------|-------------------------------|
| Tài khoản | `employee_id = NULL` | `employee_id` 1-1 với employee |
| Job position | Không gắn | Bắt buộc qua employee |
| Được làm | Quản trị tài khoản `sys_user`; cấu hình ma trận `position_permission` | Nghiệp vụ theo permission của chức danh (employee, attendance, payslip, …) |
| Không được làm | **Tham gia nghiệp vụ doanh nghiệp** (xem/sửa hồ sơ NV, import công, xem lương…) | **Sửa ma trận phân quyền / CRUD tài khoản hệ thống** |
| Home / sidebar | Chỉ module quản trị hệ thống | Module nghiệp vụ suy từ `permissions` của `job_position` |

**Nguyên tắc cứng**: Filter và Service phải tách URL/module theo realm. ADMIN sửa được ma trận permission–job_pos nhưng **không** được gọi API nghiệp vụ. USER làm nghiệp vụ nhưng **không** vào màn hình admin hệ thống.

### 4.1c Home & Sidebar (không dùng bảng menu)

- `/home` whitelist sau login.
- Backend build lưới chức năng + sidebar từ:
  - **USER**: map `UserSession.permissions` → danh sách module/action được phép (convention URL trong `05`).
  - **ADMIN**: danh sách cố định module hệ thống (sys-user, permission-matrix, … — chi tiết trong module admin docs).
- Khi ADMIN đổi `position_permission`, home/sidebar của nhóm chức danh đó **đổi theo** sau `PositionPermissionMatrix.reload()` (và/hoặc user re-login tùy MVP).

### 4.2 SessionManager (khái niệm)

`SessionManager` bọc `HttpSession` để lưu/đọc/xóa `UserSession` (get / set / remove / isLoggedIn). Chữ ký method chi tiết xem code hạ tầng — module không cần định nghĩa lại.

### 4.3 UserSession Fields
```
UserSession {
    Long userId;
    String username;
    Set<String> roles;           // đúng một: ADMIN hoặc USER
    Long employeeId;             // null nếu ADMIN
    Long positionId;             // null nếu ADMIN
    Long departmentId;           // null nếu ADMIN
    Integer dataScope;           // null nếu ADMIN; 1=SELF, 2=DEPARTMENT, 3=ALL nếu USER
    Set<String> permissions;     // null/rỗng nếu ADMIN; từ position_permission nếu USER
}
```

### 4.4 Session Constraints
- Session timeout: 30 phút (rolling) — `web.xml`.
- Password: BCrypt (`org.mindrot.jbcrypt.BCrypt`).

---

## 5. Authorization (Position-centric)

Tham chiếu ADR-0001.

### 5.1 Hai trục (role ⊥ job_pos)

> **Role độc lập hoàn toàn với `job_position`.** `role` chỉ phân biệt realm tài khoản. Quyền nghiệp vụ và DataScope chỉ áp dụng cho USER.

| Trục | Nguồn | Áp dụng |
|------|-------|---------|
| System Role | `role` / `user_role` | Nhánh Filter: ADMIN vs USER |
| Feature permission | `position_permission` | Chỉ USER — action được gọi |
| DataScope | `job_position.data_scope` | Chỉ USER — phạm vi dữ liệu khi list/paging |

### 5.2 Phạm vi ADMIN (realm hệ thống)

ADMIN **chỉ** làm việc hệ thống: **CRUD `sys_user`** và **quản lý ma trận `position_permission`** (phân quyền động theo chức danh → quyết định module hiện trên home/sidebar của nhóm). ADMIN **không** tham gia nghiệp vụ doanh nghiệp (employee, attendance, payslip, contract, duyệt đơn…).

> Luồng "HR tạo employee mới → thông báo ADMIN → tạo `sys_user` theo mã nhân viên" là **liên module**; chi tiết do module employee + admin/sys-user mô tả, không hard-code trong Core.

### 5.3 AuthFilter (MVP ưu tiên #1)

`AuthFilter` (`@WebFilter("/*")`) là chốt chặn trước mọi Servlet. Nguyên tắc:

- Chưa đăng nhập → redirect `/login` (trừ whitelist: `/login`, `/static/*`, `/home`).
- Tách **realm** theo session: ADMIN chỉ vào URL hệ thống, USER chỉ vào URL nghiệp vụ — không đan xen.
- Với USER, quyền action tra từ `PositionPermissionMatrix` (nguồn: `position_permission`).
- Thiếu quyền hoặc sai realm → trang **403** lập trình (`ErrorController`).

> Bảng ánh xạ URL→permission và danh sách prefix realm do tài liệu module định nghĩa; Core chỉ enforce cơ chế tách nhánh.

### 5.4 PositionPermissionMatrix (khái niệm)

Cache tra cứu URL ↔ permission: nạp lúc khởi động (`load`), tra cứu khi request (`hasPermission`), làm mới sau khi ADMIN đổi ma trận (`reload`). Chữ ký chi tiết xem code hạ tầng.

**Mô hình tham chiếu**:
1. Nguồn chân lý nghiệp vụ: `permission` + `position_permission` + `job_position`.
2. Lúc login USER: JOIN employee → job_position → position_permission → nạp `permissions` + `dataScope` vào session.
3. Home/sidebar USER = projection của `permissions` (backend), không đọc bảng menu.

---

## 6. Data Permission (DataScope)

### 6.1 Nguyên tắc
- Data Permission thực hiện **ở Service Layer**.
- Giá trị mặc định lấy từ `job_position.data_scope` của user (đã có trên `UserSession`).
- `DataScopeResolver` map số nguyên → điều kiện SQL.

### 6.2 Position DataScope (DB)
| Giá trị | Ý nghĩa |
|--------:|---------|
| 1 SELF | Chỉ dữ liệu của bản thân |
| 2 DEPARTMENT | Dữ liệu trong phòng ban của user |
| 3 ALL | Toàn công ty (không thêm filter) |

> Câu điều kiện SQL cụ thể theo từng resource do tài liệu module mô tả (chỉ dựng từ `UserSession`).

### 6.3 DataScope POJO / ScopeType
```
DataScope { ScopeType type; List<Long> ids; String condition; }
ScopeType { ALL, SELF, DEPARTMENT, LIST, CONDITION }
```
- `DEPARTMENT` là giá trị chính thức tương ứng DB = 2.
- `LIST` / `CONDITION` dành cho mở rộng module (không dùng trong Core MVP trừ khi cần).

### 6.4 DataScopeResolver
```
DataScope resolve(UserSession user, String resourceType);
```
- Chỉ gọi cho **USER**. Map `user.dataScope` → DataScope (SELF / DEPARTMENT / ALL).
- **ADMIN không resolve DataScope nghiệp vụ** — Filter đã chặn URL nghiệp vụ trước khi vào Service.

### 6.5 Bảo mật
- `DataScope.condition` **chỉ** xây từ dữ liệu nội bộ (`UserSession`).
- **Cấm** lấy tham số từ `HttpServletRequest` để tạo condition.
- MyBatis: `#{}` cho tham số; `${}` chỉ cho condition đã kiểm soát.

---

## 7. Exception & Validation Handling

### 7.1 Exception Hierarchy
- `BusinessException` (RuntimeException)
- `ValidationException` extends BusinessException
- `UnauthorizedException` extends BusinessException

### 7.2 Validation phân cấp
- **Controller**: null/rỗng/format → set `errorMessage` + forward. **Không throw**.
- **Service**: business rules → throw `ValidationException`.

### 7.3 Trang lỗi lập trình (ErrorController)

Thuần Servlet: `web.xml` khai báo `<error-page>` theo mã trạng thái → `ErrorController` forward sang JSP lỗi trong `/WEB-INF/views/common/`.

| Tình huống | Trang đích |
|-----------|-----------|
| Ngoài quyền / sai realm (`AuthFilter`, `UnauthorizedException`) | `error-403.jsp` |
| Không tìm thấy tài nguyên (404) | `error-404.jsp` |
| `ValidationException` | forward lại form kèm `errorMessage` |
| `BusinessException` / Runtime khác | log ERROR → `error-500.jsp` |

> **403 bắt buộc**: USER cố mở màn hình cấp trên/HR, hoặc cố vào màn hình ADMIN → luôn về `error-403.jsp`, **không** redirect `/login` (tránh hiểu nhầm là chưa đăng nhập).

---

## 8. Transaction Management (SqlExecutor)

### 8.1 Pattern
Mỗi Service method public = 1 Unit of Work: begin → mapper ops → commit; catch → rollback; finally → close.

### 8.2 Quy tắc
- Không gọi Mapper từ Controller.
- Domain A không gọi Mapper domain B (phải qua Service B).
- Không dùng XA / distributed transaction.

---

## 9. Request Lifecycle Flow (End-to-End)

1. GET `/employee/detail?id=1`
2. AuthFilter: session + `PositionPermissionMatrix` / `user.permissions` chứa `employee:READ`
3. Controller → Service.getDetail(user, id)
4. Service: `DataScope scope = resolver.resolve(user, "employee")` → Mapper với scope
5. Forward JSP `/WEB-INF/views/authority/employee/employee-detail.jsp` (path theo module)

**Lưu ý**: URL `/{feature}/{action}` — `{domain}` chỉ dùng package/JSP.

---

## 10. Key Design Decisions

- **Hai realm tách biệt**: ADMIN (hệ thống) ≠ USER (nghiệp vụ) — Filter cấm đan xen.
- **Position-centric authorization**: quyền nghiệp vụ gắn `job_position` qua `position_permission`.
- **System role tối giản**: chỉ `ADMIN` | `USER` (ADR-0001); role ⊥ job_pos.
- **DataScope trên position**: SELF / DEPARTMENT / ALL — chỉ USER; áp trước paging.
- **Home/sidebar = projection permission** (backend) — **không** bảng `menu` trong DB.
- **Trang lỗi lập trình** (403/404/500) qua `ErrorController` + `<error-page>` — thuần Servlet.
- **ADMIN**: CRUD sys-user + ma trận position_permission; **không** nghiệp vụ doanh nghiệp.
- **Không HRM/Director dashboard** trong MVP.
- **Permission Format**: `{resource}:{ACTION}` UPPERCASE trong `permission_name`; cột `action` = TINYINT code (ADR-0002).
- **Explicit Transaction**: SqlExecutor thủ công.
- **Module Independence**: Service A không gọi Mapper B.
- **TINYINT + enum code**: mọi status/action/type/scope — không CHECK list trên DB (ADR-0002).

---

## 11. Constraints

- Không dùng Spring, Hibernate, microservices.
- Tuân thủ YAGNI, Convention over Configuration.
- Mọi thay đổi kiến trúc đều cần ADR.

**Kiến trúc này là nền tảng cho Core MVP. Khi xung đột với tài liệu init khác, resolve theo Document Priority trong `01-init.md`.**
