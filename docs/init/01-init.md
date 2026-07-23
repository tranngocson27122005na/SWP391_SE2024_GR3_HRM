# 01-init.md

**Project Initiation Document**  
**HRMS for M&T Company**  
**Version:** 3.1.0  
**Date:** 2026-07-21  
**Status:** Draft for Review

---

## 1. Tầm nhìn dự án

Xây dựng hệ thống HRMS chuyên nghiệp, minh bạch và tuân thủ pháp luật lao động Việt Nam cho Công ty Sản xuất và Thương mại M&T (180 người lao động). Hệ thống thay thế hoàn toàn quy trình quản lý thủ công hiện tại (Excel + PDF) bằng một nền tảng số tích hợp, tập trung vào tính chính xác của dữ liệu và khả năng mở rộng cho các nghiệp vụ nhân sự.

---

## 2. Mục tiêu kinh doanh

- Tăng hiệu quả quản lý nhân sự từ 180 người, giảm thời gian xử lý thủ công xuống dưới 30%.
- Đảm bảo tuân thủ nghiêm ngặt Bộ luật Lao động 2019 và Nội quy Lao động của Công ty.
- Cung cấp nền tảng tự phục vụ (Self-Service) cho người lao động: mỗi USER xem được **dữ liệu của chính mình** (DataScope = SELF). MVP **không** bao gồm quy trình nộp đơn/duyệt.
- Xây dựng nền tảng có khả năng mở rộng cho các module sau này (Payroll Engine, Workflow Approval, Attendance Import...).

### 2b. MVP Out of Scope (khóa sớm — chi tiết Discovery)

Không làm trong MVP đầu: duyệt đơn 2 cấp, leave/OT/promotion/resignation workflow, HRM/Director dashboard nghiệp vụ, bảng `menu` trong DB.  
MVP ưu tiên #1: **AuthFilter** + tách rõ realm ADMIN (hệ thống) vs USER (nghiệp vụ). Xem `docs/discovery/01-mvp-scope.md`.

---

## 3. Mục tiêu kỹ thuật

- Xây dựng một codebase sạch, có cấu trúc rõ ràng bằng Java Web thuần (Servlet + JSP + MyBatis) để hiểu sâu kiến trúc phần mềm.
- Thiết lập **Documentation as Single Source of Truth** – mọi code sinh ra bởi AI phải dựa hoàn toàn vào tài liệu.
- Áp dụng **AI-First Development** với quy trình Documentation First -> Specification First -> Implementation.
- Xây dựng **Core Foundation** vững chắc bao gồm: Xác thực (Authentication), Phân quyền (AuthFilter + PositionPermissionMatrix), Quản lý Session, DataScope Framework (theo `job_position.data_scope`), Exception Handling, Transaction Management (SqlExecutor), và Logging.

---

## 4. Lý do chọn cách tiếp cận

- **Java Servlet + JSP + MyBatis**: Giúp đội ngũ nắm vững nền tảng web Java truyền thống, tránh magic của framework nặng.
- **DDD Lite + MVC mở rộng**: Kết hợp sức mạnh tổ chức domain với sự đơn giản của mô hình MVC quen thuộc.
- **Documentation First**: Đảm bảo tính nhất quán, dễ bảo trì và chuyển giao kiến thức.
- **Agile + Foundation-first**: Giảm rủi ro bằng cách triển khai từng phần nhỏ, có giá trị và có thể kiểm chứng ngay.

Các module phức tạp (Payroll Calculation Engine, Full Workflow Approval, Recruitment, KPI...) sẽ được triển khai ở các Sprint sau.

---

## 5. Phạm vi Core Foundation

Core Foundation là tập hợp các thành phần **không chứa logic nghiệp vụ** và được dùng chung cho toàn hệ thống:

### 5.1 Identity & Access Management
- Chuỗi quan hệ: `department → job_position → employee ↔(1-1) sys_user → role`; `job_position ↔(N:N) permission` qua `position_permission`.
- System role chỉ `ADMIN` | `USER` — **hai realm không đan xen** (xem `04` §4.1b).
- ADMIN: CRUD `sys_user` + ma trận `position_permission`; **không** làm nghiệp vụ doanh nghiệp.
- USER: quyền nghiệp vụ + DataScope từ `job_position`; **không** quản trị hệ thống.
- Bảng hạ tầng: `department`, `job_position`, `sys_user`, `role`, `user_role`, `permission`, `position_permission`. **Không** bảng `menu`.

### 5.2 Authorization Engine (Position-centric)
- AuthFilter (MVP ưu tiên #1): tách URL theo realm ADMIN vs USER; USER check `position_permission`.
- PositionPermissionMatrix: load/hasPermission/reload (ADR-0001, `04` §5).
- Home/sidebar: backend suy từ permissions — không đọc bảng menu.

### 5.3 DataScope Framework
- Chỉ USER: `job_position.data_scope` (1=SELF, 2=DEPARTMENT, 3=ALL) nạp lúc login; filter **trước** phân trang.
- ADMIN: không áp DataScope nghiệp vụ (bị chặn URL nghiệp vụ).

### 5.4 Session Management
- SessionManager: get/set/remove UserSession.
- UserSession: xem `04` §4.3 (ADMIN không có employee/position/permissions nghiệp vụ).
- Session timeout: 30 phút.

### 5.5 Common Infrastructure
- **Exception Handling**: BusinessException hierarchy, handler tập trung.
- **Transaction Management**: SqlExecutor (begin/commit/rollback).
- **Logging**: SLF4J + Logback.

---

## 6. Thành công của dự án

Dự án thành công khi:
- Toàn bộ tài liệu docs/ trở thành nguồn chân lý duy nhất.
- Core Foundation hoạt động ổn định.
- Module mới có thể tích hợp mà không cần sửa code core.
- Codebase tuân thủ nghiêm ngặt architecture và convention đã định nghĩa.

---

## 7. Ràng buộc thuật ngữ (Terminology Constraint)

Tất cả User Story, Epic, Task và Acceptance Criteria phải sử dụng đúng thuật ngữ đã định nghĩa trong:

docs/init/07-glossary.md

Không được:
- Đổi tên business term.
- Sử dụng từ đồng nghĩa.
- Tự đặt role mới.
- Tự đổi tên entity.

Nếu xuất hiện thuật ngữ mới:
1. Cập nhật 07-glossary.md.
2. Review.
3. Sau đó mới được sử dụng trong Sprint.

---

## 8. Hướng dẫn thêm Module mới

**Template chuẩn**: `docs/modules/tmpl/` (copy 4 file vào `docs/modules/{domain}/`).

**Quy trình** (chi tiết trong `09-development-workflow.md`):
1. **Discovery** (tuỳ chọn nhưng khuyến nghị): hình thành bài toán trong `docs/discovery/{topic}/` — nội dung do BA/team tự cập nhật.
2. Promote thuật ngữ mới vào `07-glossary.md` (nếu có) và được phê duyệt.
3. Viết 4 file module theo template:
   - `01-specification.md`: FR, Actors, Permissions, Business Rules.
   - `02-database.md`: Bảng, cột, khóa ngoại, permission seeds.
   - `03-design.md`: URL, JSP, Controller, DTO.
   - `04-implementation.md`: Pseudocode, method signatures.
4. Phê duyệt → AI/dev sinh code.

**Lưu ý**: Không tự đặt system role mới. Danh sách hợp lệ: `ADMIN` | `USER` (`07-glossary.md`). Chức danh nghiệp vụ là `job_position`, không phải role.

---

## 9. Tài liệu bất biến

Toàn bộ `docs/init/` là bất biến trong phạm vi trách nhiệm từng file. Mọi thay đổi về kiến trúc nền tảng phải được ghi nhận bằng ADR trong `docs/decisions/`.

## 10. Document Priority (nguồn resolve xung đột duy nhất)

Khi có mâu thuẫn giữa các tài liệu trong `docs/init/`, **chỉ** bảng dưới đây được dùng để resolve. Các file khác không được tự nhận “ưu tiên cao nhất”.

| Ưu tiên | File | Phạm vi resolve |
|--------:|------|-----------------|
| 1 | `04-architecture.md` | Kiến trúc, Auth/RBAC/DataScope, transaction, lifecycle |
| 2 | `03-project-structure.md` | Vị trí package, thư mục, layer |
| 3 | `05-coding-convention.md` | Naming, URL/JSP/DTO, validation/logging |
| 4 | `07-glossary.md` | Thuật ngữ, Role, Permission Action |
| 5 | `01-init.md` | Tầm nhìn, phạm vi Core, quy trình tổng quát |
| 6 | `02-technology-stack.md` | Công nghệ cho phép / cấm |
| 7 | `06-ai-contract.md` | Ràng buộc hành vi AI |
| 8 | `08-project-principles.md` | Nguyên tắc văn hóa (không override kỹ thuật) |
| 9 | `09-development-workflow.md` | Quy trình Sprint / DoR / DoD |

**Quy tắc**: File ưu tiên thấp hơn phải chỉnh cho khớp file ưu tiên cao hơn khi phát hiện xung đột.

**Tài liệu này định nghĩa tầm nhìn và bảng ưu tiên resolve xung đột của bộ `docs/init/`.**