# 03-project-structure.md

**Project Structure**  
**HRMS for M&T Company**  
**Version:** 3.2.0  
**Date:** 2026-07-21  
**Status:** Draft for Review

---

## 1. Thư mục gốc

SWP391_SE2024_GR3_HRM/
├── docs/                    # Single Source of Truth
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.hrm/
│   │   ├── resources/
│   │   └── webapp/
│   └── test/
├── sql/                     # Script database & migration
├── pom.xml
└── README.md

---

## 2. Cấu trúc Source Code (Java)

com.hrm/
│
├── controller/                   # MVC Controller – Định tuyến, KHÔNG business logic
│   ├── common/                   # Điều phối chung toàn hệ thống
│   │   ├── LoginController.java
│   │   ├── LogoutController.java
│   │   ├── HomeController.java
│   │   └── ErrorController.java
│   │
│   └── {domain}/                 # Tên domain (authority, attendance, payroll...)
│       └── {feature}/           # Nhóm chức năng (employee, department, payslip...)
│           └── {Action}{Feature}Controller.java   # VD: DetailEmployeeController.java
│
├── service/                      # Business Logic + Transaction
│   ├── common/                   # Service dùng chung, KHÔNG logic domain
│   │   ├── SecurityService.java      # BCrypt hash/verify password
│   │   └── AuthService.java          # Xác thực đăng nhập, build UserSession
│   │
│   └── {domain}/                 # Domain (authority, payroll, attendance...)
│       ├── EmployeeService.java     # VD thực tế: service/authority/
│       ├── DepartmentService.java   # + ContractService, JobPositionService, PermissionMatrixService
│       └── PayslipService.java      # VD thực tế: service/payroll/ (+ PayrollRunService)
│
├── dto/                          # Data Transfer Objects
│   ├── request/
│   │   ├── {Resource}FormRequest.java       # Dùng chung Create/Update (VD: EmployeeFormRequest.java)
│   │   └── {Resource}UpdateRequest.java     # Chỉ khi Update có field bắt buộc thêm (xem 05)
│   ├── response/
│   │   └── {Resource}Response.java          # VD: EmployeeResponse.java
│   └── session/
│       └── UserSession.java      # userId, username, roles, employeeId, positionId, departmentId, dataScope, permissions
│
├── persistence/                  # Data Access Layer (MyBatis)
│   ├── entity/
│   │   ├── {Entity}.java         # Anemic entity, ánh xạ 1-1 với bảng DB
│   │   └── enums/                # Business enum (thuộc domain) — ví dụ minh họa vị trí đặt
│   │       ├── EmploymentGroup.java  # OFFICE, FACTORY
│   │       ├── ContractType.java     # FULL_TIME, PROBATION...
│   │       └── PayslipStatus.java    # DRAFT, APPROVED, PAID, CANCELLED...
│   │
│   └── mapper/
│       └── {Entity}Mapper.java   # MyBatis interface (VD: EmployeeMapper.java)
│
├── infrastructure/               # Tầng hạ tầng, KHÔNG logic nghiệp vụ
│   ├── security/                 # Xác thực & phân quyền (core Servlet Filter)
│   │   ├── AuthFilter.java                  # @WebFilter("/*") — auth + tách realm ADMIN/USER
│   │   ├── SessionManager.java
│   │   ├── PositionPermissionMatrix.java    # Cache permission→URL (nạp lúc Filter.init/startup)
│   │   ├── DataScopeResolver.java           # Interface
│   │   ├── AuthorityDataScopeResolver.java  # Impl: map dataScope từ UserSession (chỉ USER)
│   │   └── DataScope.java                   # POJO: type, ids, condition
│   │
│   ├── persistence/              # Core DB executor
│   │   └── executor/
│   │       └── SqlExecutor.java
│   │
│   ├── exception/                # Custom exception
│   │   ├── BusinessException.java
│   │   ├── ValidationException.java
│   │   └── UnauthorizedException.java
│   │
│   └── enums/                    # Technical enum (cơ chế kỹ thuật)
│       └── ScopeType.java        # ALL, SELF, DEPARTMENT, LIST, CONDITION
│                                 # (action lưu dạng chuỗi trong bảng permission — không cần enum riêng ở Core)
│
└── utility/                      # Helper thuần túy, KHÔNG gọi DB, KHÔNG business
    ├── DateUtils.java
    ├── StringUtils.java
    ├── Paging.java
    └── validation/
        └── ValidationUtils.java      # isValidEmail(), isValidPhone()...

---

## 3. Cấu trúc Resources

src/main/resources/
├── mapper/ # MyBatis XML mapping (khớp tên với interface)
│   └── {Entity}Mapper.xml # VD: EmployeeMapper.xml
├── mybatis-config.xml # Cấu hình MyBatis (datasource, typeAliases)
├── logback.xml # Cấu hình logging
└── application.properties # Cấu hình ứng dụng

---

## 4. Cấu trúc Web Resources

src/main/webapp/
├── static/
│   ├── css/
│   │   ├── bootstrap.min.css
│   │   └── custom.css
│   └── js/
│       ├── jquery.min.js
│       ├── bootstrap.bundle.min.js
│       └── app.js
│
└── WEB-INF/
    ├── web.xml # Deployment descriptor: session-config + <error-page> (403/404/500 → ErrorController)
    ├── fragment/ # Layout dùng chung (LƯU Ý: 'fragment' số ít, khớp code)
    │   ├── top-nav.jsp
    │   └── side-bar.jsp # Sidebar suy từ permission của USER (backend), KHÔNG hard-code theo role
    └── views/ # 1 màn hình = 1 file .jsp
        ├── common/ # login.jsp, home.jsp, error-403.jsp, error-404.jsp, error-500.jsp
        └── {domain}/ # Tên domain (authority, payroll...)
            └── {feature}/ # Nhóm chức năng (employee, department, job-position...)
                └── {feature}-{action}.jsp # kebab-case, VD: employee-detail.jsp

**Quy tắc đặt tên JSP**: `{feature}-{action}.jsp` (toàn bộ kebab-case, giữ nguyên dấu gạch ngang có sẵn trong `{feature}`). Các action thường gặp (VD: feature=`employee`):
- `employee-list.jsp` – danh sách
- `employee-detail.jsp` – chi tiết
- `employee-create-form.jsp` – form tạo mới
- `employee-edit-form.jsp` – form sửa
- `employee-confirm-delete.jsp` – xác nhận xóa
- `employee-import-form.jsp` – form upload import

**VD cho feature có tên ghép**: feature=`job-position`, action=`list` → `job-position-list.jsp` (giữ nguyên dấu gạch ngang trong tên feature).
---

## 5. Cấu trúc Documentation (docs/)

docs/
├── init/                    # Bất biến - Nền tảng dự án (9 files)
│   ├── 01-init.md
│   ├── 02-technology-stack.md
│   ├── 03-project-structure.md
│   ├── 04-architecture.md
│   ├── 05-coding-convention.md
│   ├── 06-ai-contract.md
│   ├── 07-glossary.md
│   ├── 08-project-principles.md
│   └── 09-development-workflow.md
│
├── modules/                 # Tài liệu từng module
│   ├── tmpl/                # Template generic (copy vào {domain}/)
│   │   ├── 01-specification.md
│   │   ├── 02-database.md
│   │   ├── 03-design.md
│   │   └── 04-implementation.md
│   └── {domain}/
│       ├── 01-specification.md
│       ├── 02-database.md
│       ├── 03-design.md
│       └── 04-implementation.md
│
├── discovery/               # Problem framing (team tự cập nhật; trước khi viết module)
├── backlog/                 # Product backlog & sprint documents
├── decisions/               # Architecture Decision Records (ADR)
└── release/                 # Release notes & deployment

---

## 6. Phân biệt các Layer

| Layer | Vị trí | Trách nhiệm | Liên quan DB | Business logic |
|-------|--------|-------------|--------------|----------------|
| Controller | controller/ | Nhận request, gọi Service, forward/redirect JSP | Không | Không |
| Service | service/ | Business logic, transaction boundary, gọi Mapper | Có | Có |
| Common Service | service/common/ | Chức năng kỹ thuật dùng chung ( password) | Có thể | Không |
| Persistence | persistence/ | Mapper, Entity, Business Enum | Có | Không |
| Infrastructure | infrastructure/ | Cơ chế hạ tầng (Filter, SqlExecutor, Exception) | Có thể | Không |
| Utility | utility/ | Helper thuần túy (Date, String, Validation) | Không | Không |
| DTO | dto/ | Trung chuyển dữ liệu giữa Controller <-> Service | Không | Không |

---

## 7. Nguyên tắc tổ chức

- **Package theo domain và feature**: `controller.authority.employee` (domain=authority, feature=employee); service gom theo domain: `service.authority`, `service.payroll`.
- **Mỗi module** có thư mục riêng trong docs/modules/ và source code tương ứng.
- **Controller chỉ định tuyến** – không chứa business logic.
- **Service chứa toàn bộ business logic và transaction boundary**.
- **Infrastructure** chứa thành phần dùng chung, KHÔNG phụ thuộc domain.
- **Utility** chứa helper thuần túy, KHÔNG gọi DB.
- **Business Enum** nằm trong persistence/entity/enums/ (gắn với entity).
- **Technical Enum** nằm trong infrastructure/enums/ (cơ chế kỹ thuật).
- **Không truy cập trực tiếp vào Mapper từ Controller** – phải qua Service.

---

## 8. Quy tắc mở rộng cấu trúc

Khi thêm module mới:
1. Tạo package trong controller/{domain}/, service/{domain}/, persistence/entity/.
2. Tạo thư mục views trong WEB-INF/views/{domain}/.
3. Thêm permission mới vào DB (`permission`) và gán qua `position_permission` (chức danh). System role chỉ ADMIN/USER.
4. **Không sửa** code trong infrastructure/ trừ khi thay đổi nền tảng (cần ADR).

---

## 9. Cơ chế Servlet thuần (core Servlet/JSP — không framework)

Dự án dùng **hoàn toàn Servlet API + JSP/JSTL**, không Spring/Struts, để hiểu rõ vòng đời request và tối ưu tốc độ. (Đây là mức nguyên tắc; chi tiết từng màn hình do module mô tả.)

- **Controller = 1 `HttpServlet`**: mỗi action là class `extends HttpServlet`, khai báo URL bằng `@WebServlet("/{feature}/{action}")` — không dùng DispatcherServlet/front-controller của framework. VD: `@WebServlet("/employee/list")`.
- **View**: Controller `setAttribute(...)` rồi `RequestDispatcher.forward` sang JSP trong `/WEB-INF/views/...`. JSP chỉ dùng **JSTL + EL**, không scriptlet nghiệp vụ.
- **Filter**: `AuthFilter` (`@WebFilter("/*")`) chặn mọi request để xác thực + tách realm ADMIN/USER trước khi tới Servlet.
- **Khởi tạo cache**: `PositionPermissionMatrix` nạp lúc `Filter.init()` (hoặc `ServletContextListener`) khi ứng dụng start.
- **Trang lỗi lập trình**: `web.xml` khai báo `<error-page>` cho 403/404/500 → `ErrorController` forward sang `error-{code}.jsp`. Truy cập ngoài quyền/realm luôn về **403** (xem `04` §7).
- **Không** dùng DI annotation, ORM tự động, hay template engine ngoài JSP.

**Cấu trúc này là nền tảng tổ chức codebase. Khi xung đột với tài liệu init khác, resolve theo bảng Document Priority trong `01-init.md`.**