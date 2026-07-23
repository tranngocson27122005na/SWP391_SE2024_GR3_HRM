# 02-database.md - {Tên Module}

**Module:** {Module Name}  
**Domain:** `{domain}`  
**Version:** 1.0.0 | **Date:** YYYY-MM-DD | **Status:** Draft  
**Schema nguồn:** `sql/0.hrmdb.sql` (database `hrmdb`) — DB-first; lệch → cập nhật SQL + ADR nếu đổi Core

---

## 1. Global column rules (khớp Core / ADR-0002)
- PK: `{entity}_id` INT AUTO_INCREMENT (VD: `employee_id`, `department_id`)
- Soft-delete: `status` TINYINT DEFAULT 1 — nghĩa enum `ActiveStatus` (code ở Java; DB chỉ số)
- Audit list: `created_at` DATETIME/TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- Workflow (nếu có): `{concept}_status` **TINYINT** + enum Java — **không** dùng tên `status`
- Các cột type/action/scope: **TINYINT**; nghĩa ở `persistence.entity.enums` / technical enums
- FK nghiệp vụ: cùng tên cột với PK bảng cha (VD: `position_id` → `job_position.position_id`)
- FK audit user: `updated_by` / `granted_by` / `imported_by` → `sys_user.user_id`
- **Không** CHECK list giá trị trên DB (ADR-0002)

## 2. Table Definitions

*Chỉ mô tả bảng module thêm mới hoặc cột module bổ sung. Bảng Core đã có → tham chiếu `0.hrmdb.sql`, không copy DDL đầy đủ trừ khi cần làm rõ.*

### 2.1 `{table_name}` (mới / thay đổi)
| Column | Type | Attributes | Description |
| :--- | :--- | :--- | :--- |
| `{entity}_id` | INT | PK, AUTO_INCREMENT | Khóa chính |
| `…` | TINYINT / … | … | … (code → enum `…`) |
| `status` | TINYINT | NOT NULL, DEFAULT 1 | Soft-delete ActiveStatus |
| `created_at` | DATETIME | DEFAULT CURRENT_TIMESTAMP | Ngày tạo |

**Indexes / Unique**: …  
**Foreign Keys**: nêu ràng buộc (không bắt buộc paste ALTER dài).

## 3. Permission Seeds

*Chỉ khi module có permission nghiệp vụ. Catalog: `permission_name` = `{feature}:{ACTION}`; cột `action` = TINYINT (số do enum Java).*

```sql
-- Mẫu — số action khớp enum PermissionAction khi implement
INSERT INTO permission (permission_name, resource, action, description) VALUES
  ('{feature}:READ', '{feature}', {code_READ}, '…');
```

Gán chức danh (không dùng `role_permission`):

```sql
INSERT INTO position_permission (position_id, permission_id, granted_by)
SELECT … ;
```

*Module ADMIN-only / dùng chung:* ghi **Không seed permission** + lý do.

## 4. Home / Sidebar (không bảng menu)
- USER: mục hiện khi có permission tương ứng (backend suy từ session/cache).
- ADMIN: mục hệ thống cố định (liệt kê label ý + URL feature nếu cần).
- **Không** seed bảng `menu`.
