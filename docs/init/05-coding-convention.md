# 05-coding-convention.md

**Coding Convention**  
**HRMS for M&T Company**  
**Version:** 3.3.0  
**Date:** 2026-07-22  
**Status:** Draft for Review

---

## 1. Naming Convention

### 1.1 Java Classes & Members
- **Class**: PascalCase (`EmployeeService`, `AuthFilter`)
- **Method**: camelCase (`getEmployeeById`, `validateRequest`)
- **Variable**: camelCase (`employeeId`, `currentUserSession`)
- **Constant**: UPPER_SNAKE_CASE (`MAX_OT_HOURS_PER_MONTH`)
- **Package**: lowercase (`controller.employee`, `infrastructure.security`)

### 1.2 Database & MyBatis
- **Table**: snake_case (`daily_attendance`, `sys_user`)
- **Column**: snake_case (`employee_id`, `password_hash`)
- **Mapper Method**: camelCase (`findByEmployeeId`, `insertAttendance`)
- **ResultMap**: Đặt tên `{Entity}ResultMap` (VD: `EmployeeResultMap`)

### 1.3 JSP
- **File name**: kebab-case theo công thức `{feature}-{action}.jsp` (VD: `employee-detail.jsp`, `leave-request-list.jsp`). Xem §3b.3.
- **Form fields**: snake_case hoặc camelCase nhưng phải nhất quán trong toàn project.

### 1.4 DTO (Data Transfer Objects)

- **Request DTO**: `{Resource}Request` (VD: `LoginRequest`)
- **Response DTO**: `{Resource}Response` (VD: `EmployeeResponse`)
- **Vị trí**: `dto.request` cho Request, `dto.response` cho Response.
**Nguyên tắc chung**: Tên DTO là danh từ (tên Business Resource), KHÔNG chứa động từ/Action, 
trừ đúng 1 ngoại lệ dưới đây (khi cùng 1 Resource cần 2 DTO khác field cho Create/Update).

**Quy tắc chọn DTO cho Create vs Update**:

- **Luôn dùng `{Resource}FormRequest`** (VD: `EmployeeFormRequest`) cho cả Create và Update, **trừ khi** Update có field **bắt buộc từ client** mà Create không có.
- **Tiêu chí "field bắt buộc từ client"**:
  - Field do hệ thống tự sinh (VD: `createdAt`, `updatedAt`, `id`) → **không tính**.
  - Field do client gửi lên (VD: `status`, `version`, `approverId`) mà Create form không có → **tính**.
- Nếu Update có field bổ sung → **bắt buộc tách ra** 2 DTO riêng:`{Resource}UpdateRequest`.
- Nếu không có field bổ sung → **bắt buộc dùng chung** `{Resource}FormRequest`.
- **Quy tắc cho action chỉ nhận id (approve, reject, delete, submit, cancel)**:
  - Các action này chỉ truyền `id` qua query param (theo §3b.1), **không cần tạo Request DTO**.
  - Controller lấy trực tiếp `request.getParameter("id")`.
- **Chỉ tạo Request DTO khi action có ≥1 field dữ liệu** bổ sung ngoài `id` (VD: một action cần `id` + `reason` → tạo DTO `ReasonRequest`; nếu chỉ có `id` → không cần).

**Ví dụ**:
- Employee: Create và Update đều có `fullName`, `email`, `phone` → dùng `EmployeeFormRequest` chung.
- Contract: Update có thêm `contractStatus` (đổi trạng thái hợp đồng) mà Create không có → tách thành `CreateContractRequest` và `UpdateContractRequest`.

---

## 2. Package Organization (Tham chiếu 03-project-structure) 
**Lưu ý**: Để tránh nhầm lẫn với Business Workflow (chuỗi trạng thái), tên package sử dụng `{feature}` (nhóm chức năng nghiệp vụ, VD: `employee`, `department`, `job-position`). 
- **Controller**: `controller.{domain}.{feature}` (VD: `controller.authority.employee`)
- **Service**: `service.{domain}` (VD: `service.authority`)
- **Service Common**: `service.common` (VD: `SecurityService`, `AuthService`)
- **DTO**: `dto.request`, `dto.response`, `dto.session`
- **Persistence**:  - `persistence.entity` (Anemic entities)  - `persistence.entity.enums` (Business enum: ContractType, PayslipStatus...)  - `persistence.mapper` (MyBatis interfaces)
- **Infrastructure**: `infrastructure.security` (AuthFilter, SessionManager, PositionPermissionMatrix, DataScopeResolver, AuthorityDataScopeResolver, DataScope); `infrastructure.persistence.executor` (SqlExecutor); `infrastructure.exception`; `infrastructure.enums` (ScopeType gồm DEPARTMENT)
- **Utility**: `utility` (DateUtils, StringUtils, Paging, ValidationUtils)
---
## 3. Code Style
---
- **Indent**: 4 spaces
- **Line length**: Tối đa 120 ký tự
- **Order of members**: Fields → Constructor → Methods (public trước private)
- **Comments**: 
  - Javadoc cho public API
  - Inline comment giải thích "tại sao" chứ không phải "làm gì"
- **Exception**: Sử dụng custom exception (`BusinessException`, `ValidationException`, `UnauthorizedException`)
---

### 3b.1 URL Pattern (Action-based cho Servlet/JSP)
**Lưu ý**: URL format `/{feature}/{action}` áp dụng cho toàn bộ hệ thống. Không dùng prefix role trong URL. **MVP không triển khai dashboard HRM/Director.**
**Quy tắc chính**:
- `{feature}` trong URL tương ứng với Business Resource (tên số ít, kebab-case).
- `{domain}` chỉ dùng để tổ chức package và JSP, **không xuất hiện trên URL**.
- URL format: `/{feature}/{action}` (VD: `/employee/detail`, `/contract/list`).
**Quy tắc đặt tên package cho feature có dấu gạch ngang**:
- Tên package Java không được chứa dấu gạch ngang (`-`).
- Khi feature name có dấu gạch ngang, bỏ dấu gạch ngang và viết liền (VD: `job-position` → `jobposition`; `permission-matrix` → `permissionmatrix`).
- Không dùng camelCase cho package (giữ nguyên chữ thường).
**Quy tắc Controller (Áp dụng cho toàn bộ URL Pattern)**:

| Action | URL | Controller class | Methods | Ghi chú |
|--------|-----|------------------|---------|---------|
| Danh sách | GET /{feature}/list | `List{Feature}Controller` | doGet | - |
| Chi tiết | GET /{feature}/detail | `Detail{Feature}Controller` | doGet | Dùng query param `id` |
| Form tạo mới + Xử lý submit | GET + POST /{feature}/create | `Create{Feature}Controller` | doGet + doPost | Ngoại lệ: 1 class xử lý cả GET và POST |
| Form sửa | GET /{feature}/edit | `Edit{Feature}Controller` | doGet | Dùng query param `id` |
| Xử lý cập nhật | POST /{feature}/update | `Update{Feature}Controller` | doPost | Dùng hidden field `id` |
| Xóa (soft delete) | POST /{feature}/delete | `Delete{Feature}Controller` | doPost | Dùng query param `id` |
| Import dữ liệu | GET + POST /{feature}/import | `Import{Feature}Controller` | doGet + doPost | Tương tự Create |
| Export dữ liệu | GET /{feature}/export | `Export{Feature}Controller` | doGet | Xuất file trực tiếp |
| Phê duyệt | POST /{feature}/approve | `Approve{Feature}Controller` | doPost | Dùng query param `id` |
| Từ chối | POST /{feature}/reject | `Reject{Feature}Controller` | doPost | Dùng query param `id` |
| Nộp đơn | POST /{feature}/submit | `Submit{Feature}Controller` | doPost | Dùng query param `id` |
| Hủy đơn | POST /{feature}/cancel | `Cancel{Feature}Controller` | doPost | Dùng query param `id` |

**Quy tắc đặt tên Controller**: `{Action}{Feature}Controller` (VD: `ListEmployeeController`, `DetailEmployeeController`).

**Ngoại lệ**: `Create{Feature}Controller` và `Import{Feature}Controller` xử lý cả GET và POST (doGet + doPost).

**Lưu ý**:
- **Không** sử dụng chung URL `/{feature}` cho nhiều method (GET/POST) như RESTful API.
- Mỗi Controller class chỉ xử lý một action duy nhất, trừ Create và Import.
- `{domain}` không xuất hiện trên URL. VD: dù JSP ở `/WEB-INF/views/authority/employee/employee-detail.jsp`, URL là `/employee/detail`.

---
### 3b.2 Ánh xạ URL Action → Permission Action

Tên action trong URL được map trực tiếp sang cột `action` của bảng `permission`:

| URL Action | Permission Action (DB) |
| :--- | :--- |
| `overview`,`list`,`detail` | `READ` |
| `create` | `CREATE` |
| `update`, `edit` | `UPDATE` |
| `delete` | `DELETE` |
| `export` | `EXPORT` |
| `import` | `IMPORT` |
| `approve`|`APPROVE` |
| `reject` | `REJECT` |
| `submit` | `SUBMIT` |
| `cancel` | `CANCEL` |

**Ví dụ kiểm tra quyền (trong AuthFilter)**:
- URL `/employee/list` → resource=`employee`, action=`READ` → yêu cầu permission `employee:READ`.
- URL `/leave-request/submit?id=5` → resource=`leave-request`, action=`SUBMIT` → yêu cầu permission `leave-request:SUBMIT`.

**Action Vocabulary Rule**:

Action names are **fixed and standardized**. Do not use synonyms or alternative names for the ten predefined actions listed above. Invalid alternatives include: `send`, `post`, `issue` (use `submit` instead); `modify` (use `update` instead); `remove` (use `delete` instead). Only the ten action names defined in this document are valid. Reference: 07-glossary.md §7.

---

### 3b.3 JSP File Naming

**Naming Formula**: `{feature}-{action}.jsp`

All file names follow kebab-case. Preserve hyphens already contained in the feature name.

**Path**: `/WEB-INF/views/{domain}/{feature}/{feature}-{action}.jsp`

**Quy tắc JSP cho từng loại action**:

| Action Type | JSP File (Example: feature = employee) | Notes |
|-------------|----------------------------------------|-------|
| List | employee-list.jsp | Display data table |
| Detail | employee-detail.jsp | Display detailed information |
| Create Form | employee-create-form.jsp | Empty form for creating data |
| Edit Form | employee-edit-form.jsp | Form pre-filled with existing data |
| Confirm Delete | employee-confirm-delete.jsp | Confirmation page before deletion |
| Approve / Reject | No JSP | Processed directly by Controller |
| Submit / Cancel | No JSP | Processed directly by Controller |
| Import | employee-import-form.jsp | File upload form |
| Export | No JSP | Controller returns file response |

**Ví dụ** (domain=`authority`, feature=`employee`):
- Form tạo mới: `/WEB-INF/views/authority/employee/employee-create-form.jsp` → URL: `GET /employee/create` → `CreateEmployeeController` → forward đến JSP.
- Chi tiết: `/WEB-INF/views/authority/employee/employee-detail.jsp` → URL: `GET /employee/detail?id=5` → `DetailEmployeeController`.
- Danh sách: `/WEB-INF/views/authority/employee/employee-list.jsp` → URL: `GET /employee/list` → `ListEmployeeController`.
- Form sửa: `/WEB-INF/views/authority/employee/employee-edit-form.jsp` → URL: `GET /employee/edit?id=5` → `EditEmployeeController`.
- Xác nhận xóa: `/WEB-INF/views/authority/employee/employee-confirm-delete.jsp` → URL: `POST /employee/delete?id=5`.
- Import dữ liệu: `/WEB-INF/views/authority/employee/employee-import-form.jsp` → URL: `GET /employee/import` → `ImportEmployeeController`.
- Feature tên ghép: `/WEB-INF/views/authority/job-position/job-position-list.jsp` → URL: `GET /job-position/list` → `ListJobPositionController`.

---

### 3b.4 Home & Sidebar (không Dashboard, không bảng menu)

**Home (Lưới chức năng)**:
- URL: `/home` – trang chủ sau login.
- Nội dung **khác nhau theo realm**:
  - **USER**: backend build danh sách module/action từ `UserSession.permissions` (position_permission).
  - **ADMIN**: chỉ hiện module quản trị hệ thống (sys-user, permission-matrix, …).
- Sidebar đồng bộ cùng nguồn (permissions / danh sách admin cố định) — **không** dùng bảng `menu` trong DB.
- `/home` **không yêu cầu permission** cụ thể (whitelist), nhưng **nội dung** đã được lọc theo realm.

**Dashboard**:
- **Không triển khai** Admin/HRM/Director dashboard nghiệp vụ trong MVP.

**Quy tắc URL**:
- Home: `/home`
- Hệ thống (ADMIN): `/{admin-feature}/{action}` (VD: `/sys-user/list`, `/permission-matrix/list` — chốt trong module docs)
- Nghiệp vụ (USER): `/{feature}/{action}` (VD: `/employee/list`, `/attendance/import`, `/payslip/detail`)

**Lưu ý**: Không có URL `/dashboard/*`. Không seed bảng menu.

---
## 3c. Validation Convention

**Luôn validate ở cả Client và Server (Server là chính)**:
- **Client-side**: dùng JavaScript (jQuery.validate) để hỗ trợ UX, nhưng không thay thế server.
- **Server-side (Phân cấp rõ ràng)**:
  1. **Controller**: Kiểm tra dữ liệu đầu vào cơ bản (null, rỗng, định dạng số/email, độ dài tối thiểu).  
     - Nếu lỗi: **set error message vào request attribute** (`request.setAttribute("errorMessage", "...")`) và **forward về form** (`request.getRequestDispatcher(...).forward(...)`).  
     - **Tuyệt đối không throw exception** ở tầng Controller. Mọi lỗi validation cơ bản đều được xử lý bằng forward về view với message lỗi.
  2. **Service**: Kiểm tra business rules (VD: username đã tồn tại, ngày không hợp lệ, số dư không đủ, trạng thái workflow).  
     - Nếu lỗi: **throw `ValidationException`** (kèm message lỗi chi tiết).
- **Ngôn ngữ lỗi**: Message hiển thị cho end-user là **Tiếng Việt**; log, code, exception name dùng **Tiếng Anh** (Tham chiếu 07-glossary).
---
## 3d. Logging Convention

- Sử dụng SLF4J + Logback.
- Mỗi class đều có: `private static final Logger log = LoggerFactory.getLogger(ClassName.class);`
- Log level:
  - `INFO` cho action thành công (VD: "User {} logged in", "Employee {} created").
  - `WARN` cho truy cập trái phép hoặc validation fail.
  - `ERROR` cho exception không recover được.
- **Không log** password hoặc thông tin nhạy cảm (CCCD, số tài khoản).

---

## 3e. Exception Handling Convention

- **Tất cả exception** từ Service đều throw subclass của `BusinessException`.
- `web.xml` khai báo `<error-page>` → `ErrorController` chuyển hướng trang lỗi lập trình:
  - `ValidationException` -> **forward về form** với thông báo lỗi (lấy message từ exception).
  - `UnauthorizedException` / thiếu quyền / sai realm -> `error-403.jsp`.
  - `BusinessException` / Runtime khác -> `error-500.jsp` với message chung (để bảo mật, không lộ stacktrace).
- Trong `Controller`, **không cần try-catch** để xử lý ngoại lệ, hãy để `ErrorController` toàn cục bắt (cấu hình trong `web.xml`).

---

## 3f. Pagination Convention

- **Bắt buộc**: Mọi bảng nghiệp vụ (bảng do module định nghĩa) phải có cột `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP.
- **Lý do**: Dùng để sắp xếp mặc định `ORDER BY created_at DESC` cho tất cả danh sách.
- **Fallback**: Nếu bảng không có `created_at`, dùng `ORDER BY id DESC`.
- **DataScope trước phân trang (bắt buộc với USER)**: Mọi danh sách phân trang của tài khoản **USER** phải áp điều kiện DataScope (theo `job_position.data_scope`) **trước** khi tính `COUNT`/`LIMIT`. Nghĩa là filter theo job_pos xảy ra trong cùng câu query, để tổng số trang phản ánh đúng phạm vi USER được thấy (SELF / DEPARTMENT / ALL). ADMIN (hệ thống) không bị filter theo job_pos.
- Sử dụng class `Paging` trong `utility` với các tham số:
  - `page`: bắt đầu từ **1** (mặc định 1).
  - `size`: số record mỗi trang, mặc định **20**.
- Trên URL: `/employee/list?page=2&size=10`.
- Mapper nhận tham số `offset` và `limit` (tính từ `page` và `size`), **cùng** điều kiện DataScope.
- **Sắp xếp (Sort)**: Trong phạm vi Core MVP, sắp xếp được cố định theo `created_at DESC` hoặc `id DESC` tại Mapper. Không cho phép client truyền `sort` động để tránh phức tạp và rủi ro bảo mật (YAGNI).
---
## 3g. Soft Delete Convention

- Mọi entity nghiệp vụ có cột `status` TINYINT (0 = INACTIVE, 1 = ACTIVE).
- Backend map qua enum **`ActiveStatus`** (`getCode()` / `fromCode`) — ADR-0002. Không dùng boolean thô làm SSoT.
- **Không xóa cứng** record khỏi database.
- Service method `delete{Resource}(id)` chỉ cập nhật `status = 0` (INACTIVE).
- Mặc định, mọi query `SELECT` đều phải có điều kiện `WHERE status = 1` (trừ khi được thiết kế đặc biệt để xem cả inactive).

---

## 3g.1 TINYINT code & Java enum (ADR-0002)

- Cột phân loại (`action`, `data_scope`, `gender`, `{concept}_status`, `status`, …) khai báo **TINYINT** trên DB — chỉ để lưu số, dễ mở rộng.
- **Số cụ thể do enum Java quyết định** (không cần tuần tự; init **không** khóa bảng map số → tên).
- Mỗi enum: `int code`, `getCode()`, `fromCode(int)`. Service validate trước ghi.
- `permission.action` = TINYINT; `permission_name` vẫn chuỗi `{resource}:{ACTION}` cho seed/UI/log.

---

## 3h. Security Reminders (DataScope & SQL Injection)

- `DataScope.condition` **không được** đến từ user input trực tiếp. Chỉ được xây dựng từ dữ liệu nội bộ (VD: `departmentId`, `employeeId` từ `UserSession`).
- Sử dụng `#{}` trong MyBatis cho mọi tham số để tránh SQL injection. Chỉ dùng `${}` trong trường hợp đặc biệt (VD: tên cột động) và đã được kiểm soát chặt chẽ.

---

## 4. Commit Convention

Format: `<type>(<scope>): <subject>`

- `feat`: Tính năng mới
- `fix`: Sửa lỗi
- `docs`: Thay đổi tài liệu
- `refactor`: Refactor code
- `test`: Thêm/sửa test
- `chore`: Công việc bảo trì

Ví dụ: `feat(security): implement authorization filter with PositionPermissionMatrix`

---

## 5. Documentation Discipline

- Mọi class quan trọng phải có comment mô tả trách nhiệm.
- **Quy trình Documentation First**:
  - Mọi thay đổi logic hoặc tính năng mới phải được **xác định và viết** trong `docs/modules/{domain}/` **trước khi** code được sinh ra hoặc sửa đổi.
  - Code chỉ được coi là hợp lệ khi tài liệu tương ứng đã được cập nhật và phê duyệt.
- Không comment code chết hoặc code tạm (TODO, FIXME) trong source chính thức.
- Source code **không phải** nguồn chân lý. Tài liệu `docs/` mới là Single Source of Truth.
---

## 6. AI Coding Rule

- AI chỉ được sinh code khi có Specification đầy đủ trong `docs/modules/` (gồm 4 file: 01-spec, 02-db, 03-design, 04-impl).
- Code sinh ra phải tuân thủ 100% convention trong file này.
- Bất kỳ vi phạm convention nào cũng phải được ghi nhận và refactor trước khi merge.

**Tuân thủ convention này là bắt buộc và được kiểm tra trong Code Review.**

---

## 7. Tham chiếu bắt buộc

- Danh sách **System Role hợp lệ**: Xem 07-glossary §4 (`ADMIN` | `USER`).
- Chức danh nghiệp vụ: `job_position` + `position_permission` — không thêm system role thay thế.
- Danh sách **Permission Action hợp lệ**: Xem 07-glossary §7.
- Không được thêm system role hoặc action mới nếu chưa cập nhật glossary và được phê duyệt.