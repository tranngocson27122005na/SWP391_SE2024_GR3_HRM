# ADR-0001 — Position-centric Authorization

**Status:** Accepted  
**Date:** 2026-07-21  
**Deciders:** Project team (HRMS M&T)  
**Related:** `docs/init/04-architecture.md`, `docs/init/07-glossary.md`, `docs/discovery/01-mvp-scope.md`, `sql/0.hrmdb.sql`

---

## Context

Init docs ban đầu mô tả RBAC role-centric với 4 role nghiệp vụ (`ADMIN`, `DIRECTOR`, `MANAGER`, `EMPLOYEE`) và `RolePermissionMatrix` (URL → roles).  

Schema thực tế và ý định thiết kế lại khác:

```
department → job_position → employee ↔(1:1) sys_user → role(ADMIN|USER)
                 ↕ (N:N)
             permission   (qua position_permission)
```

- System Admin không thuộc phòng ban (`sys_user.employee_id = NULL`).
- Quyền nghiệp vụ gắn **chức danh** (`job_position`), không gắn role DIRECTOR/MANAGER/EMPLOYEE.
- Phạm vi dữ liệu gắn `job_position.data_scope` (SELF / DEPARTMENT / ALL).

Giữ mô hình cũ trong init sẽ mâu thuẫn SSoT với database và Discovery.

## Decision

1. **System role** chỉ còn `ADMIN` | `USER` — **hai realm không đan xen**.
2. **ADMIN**: CRUD `sys_user` + quản lý ma trận `position_permission`; **cấm** URL/API nghiệp vụ doanh nghiệp.
3. **USER**: Feature permission từ `position_permission`; DataScope từ `job_position.data_scope`; **cấm** quản trị hệ thống.
4. Đổi tên runtime matrix: `RolePermissionMatrix` → `PositionPermissionMatrix`.
5. **Home/sidebar** = backend projection từ permissions (USER) hoặc danh sách module admin cố định (ADMIN). **Không** bảng `menu` trong DB (hiểu nhầm trước đây đã sửa).
6. **Không** triển khai HRM/Director dashboard trong MVP.
7. Authorization + datascope chi tiết **gộp vào** `04-architecture.md` §4–§6.
8. MVP ưu tiên #1: **AuthorizationFilter** tách realm + làm rõ nhóm người dùng nghiệp vụ vs ADMIN.

## Consequences

### Positive
- Khớp schema và ý BA: admin hệ thống ≠ nghiệp vụ.
- Ma trận động theo chức danh → home/sidebar USER đổi theo permission.
- Tránh hiểu nhầm “bảng menu” / “ADMIN thấy ALL dữ liệu nghiệp vụ”.

### Negative / Follow-up
- Module docs `authority` / `payroll` cũ cần viết lại.
- Code còn RolePermissionMatrix / AuthFilter theo role → refactor.
- Liên module: notification khi tạo employee + auto/manual tạo sys_user theo mã NV — thuộc module emp + admin (chưa viết spec).

## Alternatives considered

| Phương án | Lý do loại |
|-----------|------------|
| Giữ 4 role nghiệp vụ + RolePermissionMatrix | Lệch DB; đan xen admin/nghiệp vụ |
| Bảng `menu` trong Core | Home thực chất suy từ permission ở backend |
| ADMIN có DataScope ALL nghiệp vụ | Vi phạm tách realm — ADMIN không tham gia nghiệp vụ |
