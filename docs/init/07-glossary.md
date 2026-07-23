# 07-glossary.md

**Project Glossary**  
**HRMS for M&T Company**  
**Version:** 3.2.0  
**Date:** 2026-07-22  
**Status:** Draft for Review

---

## 1. Thuật ngữ cốt lõi (System-wide)

| Thuật ngữ | Định nghĩa |
|-----------|------------|
| **Core Foundation** | Hạ tầng cốt lõi (Auth, Session, Position-centric Authorization, DataScope, SqlExecutor, Exception, Logging). Không chứa logic nghiệp vụ. |
| **Business Module** | Nhóm chức năng nghiệp vụ (VD: Attendance, Payroll). Có thư mục `docs/modules/{domain}/`. |
| **Domain** | Bounded context cho package Java và thư mục JSP (VD: `authority`, `attendance`, `payroll`). **Không** xuất hiện trên URL. |
| **Business Resource (Feature)** | Đối tượng/chức năng trên URL và permission. Số ít, kebab-case. VD: `employee`, `contract`, `attendance`, `payslip`. |
| **Department** | Phòng ban. Cha của mọi `job_position`. |
| **Job Position** | Chức danh thuộc một department. Mang `data_scope` và tập permission qua `position_permission`. |
| **Employee** | Hồ sơ nhân sự đang làm tại công ty (gắn 1 `job_position`). |
| **Sys User** | Tài khoản đăng nhập: (a) nhân sự (`employee_id` 1-1) hoặc (b) hệ thống ADMIN (`employee_id` NULL). |
| **System Role** | Role kỹ thuật trên bảng `role`: chỉ `ADMIN` hoặc `USER`. Không phải chức danh công ty. |
| **Position Permission** | Quan hệ N:N `job_position` ↔ `permission` — ma trận phân quyền nghiệp vụ. |
| **Workflow** | Chuỗi trạng thái nghiệp vụ (VD: DRAFT → SUBMITTED → APPROVED). Cột `{concept}_status`. **MVP: treo đơn từ 2 cấp.** |
| **Hybrid Authorization** | Mô hình **2 trục độc lập**: (1) System Role phân realm (ADMIN\|USER); (2) chỉ USER — Feature permission (`position_permission`) + DataScope (`job_position.data_scope`). Realm ⊥ job_position (không pha trộn). |
| **System Realm** | Phạm vi ADMIN: tài khoản + ma trận phân quyền chức danh. Không nghiệp vụ. |
| **Business Realm** | Phạm vi USER: nghiệp vụ doanh nghiệp theo chức danh. Không admin hệ thống. |
| **Data Permission (DataScope)** | Lọc dữ liệu theo ngữ cảnh user. Nguồn chính: `job_position.data_scope`. Áp dụng ở Service. |
| **DataScope** | POJO: type (ALL/SELF/DEPARTMENT/…), ids, condition. |
| **DataScopeResolver** | Interface `DataScope resolve(UserSession user, String resourceType)`. |
| **PositionDataScope** | Giá trị DB trên `job_position.data_scope`: 1=SELF, 2=DEPARTMENT, 3=ALL. |
| **PositionPermissionMatrix** | Cache/helper kiểm tra URL ↔ permission của user (từ `position_permission`). Dùng trong AuthFilter. |
| **Self-Service** | USER tự xem dữ liệu của chính mình (thường data_scope=SELF). |
| **Single Source of Truth** | `docs/` là nguồn chân lý. |
| **Documentation First** | Viết tài liệu trước khi code. |
| **Discovery** | Problem framing trước module docs. Path: `docs/discovery/`. |
| **DDD Lite** | Domain-Driven Design mức phù hợp + Ubiquitous Language. |

---

## 2. Các thành phần kỹ thuật nội bộ

| Thành phần | Vị trí | Trách nhiệm |
|------------|--------|-------------|
| **AuthFilter** | infrastructure.security | `@WebFilter` chặn request: xác thực + tách realm ADMIN/USER |
| **SqlExecutor** | infrastructure.persistence.executor | begin/commit/rollback/close SqlSession |
| **SessionManager** | infrastructure.security | get/set/remove UserSession |
| **PositionPermissionMatrix** | infrastructure.security | load/hasPermission/reload từ position_permission |
| **DataScopeResolver** | infrastructure.security | resolve DataScope từ UserSession (+ resource) |
| **DataScope** | infrastructure.security | POJO scope |
| **ErrorController** | controller.common | Render trang lỗi lập trình (403/404/500) qua `<error-page>` |

---

## 3. Phân biệt Business Enum và Technical Enum

**Quy tắc chung (ADR-0002):** DB chỉ khai báo `TINYINT`. **Nghĩa và số cụ thể** nằm ở enum Java (`getCode` / `fromCode`) — số có thể tùy ý, không cần tuần tự; init không khóa bảng map. Không MySQL ENUM / VARCHAR semantics; không CHECK list.

### 3.1 Business Enum
- Vị trí: `persistence/entity/enums/`
- Ví dụ: `ContractType`, `PayslipStatus`, `AttendanceStatus`, `EmploymentGroup`, `Gender`, `PermissionAction`
- Cột workflow: `{concept}_status` (TINYINT) — không dùng `status` (soft-delete).

### 3.2 Technical Enum
- Vị trí: `infrastructure/enums/` hoặc `utility/enums/`
- Ví dụ: `ScopeType` (ALL, SELF, DEPARTMENT, LIST, CONDITION); `ActiveStatus` (INACTIVE / ACTIVE).

### 3.3 Permission action
- Cột DB: `permission.action` TINYINT.
- Chuỗi hiển thị / seed: `permission_name` = `{resource}:{ACTION}` (VD `employee:READ`).
- Tập tên ACTION hợp lệ (glossary §7); **mã số** chỉ định trong enum `PermissionAction` khi viết code.

### 3.4 Lưu ý
- Không đặt business enum vào infrastructure/; không đặt technical enum vào entity/enums/.
- Thêm giá trị = cập nhật enum (+ seed nếu cần) — **không** ALTER CHECK, **không** bắt buộc cập nhật bảng số trong init.

---

## 4. System Role List (Hợp lệ)

Hệ thống **chỉ** công nhận 2 role (bảng `role`):

| Role | Mô tả |
|------|-------|
| **ADMIN** | Tài khoản **hệ thống**. `employee_id = NULL`, không gắn job_position. Chỉ quản trị: CRUD `sys_user`, ma trận `position_permission`. **Không** tham gia nghiệp vụ doanh nghiệp. |
| **USER** | Tài khoản **nhân sự**. `employee_id` 1-1. Quyền + DataScope từ `job_position`. **Không** quản trị hệ thống. |

**Quy tắc**: Không thêm system role mới nếu chưa cập nhật glossary + ADR.

Chức danh nghiệp vụ (HR-MGR, HR-STF, FAC-SUP, FAC-WRK, …) là **`job_position`**, không phải `role`.

---

## 5. Phân biệt Utility và Infrastructure

| | Utility | Infrastructure |
|--|---------|----------------|
| **Vị trí** | utility/ | infrastructure/ |
| **Gọi DB?** | Không | Có thể |
| **Business logic?** | Không | Không |
| **Ví dụ** | DateUtils, Paging, ValidationUtils | AuthFilter, SqlExecutor, SessionManager, PositionPermissionMatrix |

---

## 6. UserSession Fields

```
UserSession {
    Long userId;
    String username;
    Set<String> roles;        // đúng một: ADMIN hoặc USER
    Long employeeId;          // null nếu ADMIN
    Long positionId;          // null nếu ADMIN
    Long departmentId;        // null nếu ADMIN
    Integer dataScope;        // null nếu ADMIN; 1/2/3 nếu USER
    Set<String> permissions;  // rỗng/null nếu ADMIN; từ position_permission nếu USER
}
```

---

## 7. Permission Format

- **Tên hiển thị / seed / session string:** `{resource}:{ACTION}` (VD `employee:READ`).
- **Cột DB `permission.action`:** TINYINT — số do enum Java gán (ADR-0002; init không khóa map số).
- `resource`: kebab-case (VD: `employee`, `contract`, `attendance`, `payslip`)
- `ACTION` (đóng, UPPERCASE): CREATE, READ, UPDATE, DELETE, IMPORT, EXPORT, APPROVE, REJECT, SUBMIT, CANCEL

**Casing trong `permission_name`:** `employee:READ` hợp lệ; `employee:read` không hợp lệ.

Quyền nghiệp vụ gán qua **`position_permission`**, không qua system role DIRECTOR/MANAGER/EMPLOYEE (đã loại bỏ).

Thao tác dùng chung mọi người đã login (login/logout/home/đổi MK/trang lỗi) **không** đưa vào catalog permission.

---

## 8. Nguyên tắc sử dụng

- Dùng thống nhất glossary này.
- Thuật ngữ mới → cập nhật file này → review → mới dùng trong Sprint.
- Code/log/exception: Tiếng Anh; message end-user: Tiếng Việt.

## 9. Error Message Language

- End-user: Tiếng Việt.
- Code, log, exception name, enum: Tiếng Anh.

**Glossary này là tài liệu tham chiếu bắt buộc trong toàn bộ dự án.**
