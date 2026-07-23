# 04-implementation.md - {Tên Module}

**Module:** {Module Name}  
**Domain:** `{domain}`  
**Feature(s):** `{feature}` …  
**Version:** 1.0.0 | **Date:** YYYY-MM-DD | **Status:** Draft

---

## 1. Package Structure
- Controller: `com.hrm.controller.{domain}.{featureSansHyphen}` (common → `controller.common`)
- Service: `com.hrm.service.{domain}` (common → `service.common`)
- Mapper / Entity: `com.hrm.persistence.mapper` / `…entity`
- Business enum: `com.hrm.persistence.entity.enums` (`getCode` / `fromCode` — ADR-0002)
- Technical enum: `com.hrm.utility.enums` / `com.hrm.infrastructure.enums` (VD `ActiveStatus`, `ScopeType`)
- Security: `AuthFilter`, `PositionPermissionMatrix`, `DataScopeResolver` — chỉ đụng khi module Core/auth

## 2. Method Signatures (liệt kê đủ class sẽ sinh — YAGNI)

```
{Feature}Service
  List<{Feature}Response> getList(UserSession user, Paging paging, …)
  {Feature}Response getDetail(UserSession user, Long id)
  // chỉ thêm method có trong Spec:
  // void create(…) / update(…) / delete(…)
```

*Module không Service riêng (VD forward JSP lỗi):* ghi N/A + class Controller liên quan.

## 3. Implementation Notes (chỉ logic khác pattern mặc định)

### 3.1 Service
- Transaction: mỗi method public = 1 UoW qua `SqlExecutor` (khi có ghi DB).
- DataScope: chỉ **USER** nghiệp vụ — `resolve(user, "{feature}")` trước list/detail; ADMIN không gọi cho nghiệp vụ.
- Soft-delete: `delete` → `status =` code INACTIVE (`ActiveStatus`).
- TINYINT: convert enum ↔ code ở Service (hoặc TypeHandler nếu đã có); entity generator thường để `Byte`/`Integer`.
- Ma trận: sau UPDATE `position_permission` → `PositionPermissionMatrix.reload()`.

### 3.2 Mapper XML
- `SELECT` mặc định: `AND status = 1` (trừ màn xem inactive có chủ đích).
- DataScope: `${condition}` chỉ từ dữ liệu nội bộ Session/Resolver.
- Tham số: `#{}`.

### 3.3 Controllers
- Khớp URL `03-design.md`; mỗi action một `@WebServlet` (trừ Create/Import GET+POST).
- Validate đầu vào: `errorMessage` + forward; không throw.
- Sai realm / thiếu quyền: để Filter / `ErrorController` → 403 (không redirect login nếu đã có session).

## 4. Checklist trước khi AI code
- [ ] Class/method §2 khớp `01` + `03`
- [ ] Actor = realm / job_position; không DIRECTOR/MANAGER/EMPLOYEE
- [ ] Permission `{feature}:{ACTION}` UPPERCASE; dùng chung / ADMIN-only ghi rõ không seed nhầm
- [ ] Không bảng menu; không `role_permission`
- [ ] PK/FK/TINYINT khớp `hrmdb` + ADR-0002
- [ ] Không thêm URL/action ngoài Specification
