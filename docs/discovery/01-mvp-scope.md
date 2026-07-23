# 01-mvp-scope.md — Phạm vi MVP & mô hình phân quyền

**Status:** Aligned with init (clarified 2026-07-21)  
**Date:** 2026-07-21

---

## 1. Triết lý MVP

- Tối thiểu, đủ dùng.
- **Ưu tiên #1:** AuthorizationFilter + làm rõ **hai realm** (ADMIN hệ thống ≠ USER nghiệp vụ) — không đan xen.
- Treo: duyệt 2 cấp, đơn từ, HRM dashboard.
- **Không** bảng `menu` trong DB — home/sidebar suy từ permission ở backend.

---

## 2. Hai realm (đã promote vào `04` §4.1b / §5)

| | ADMIN | USER |
|--|-------|------|
| Việc được làm | CRUD `sys_user`; sửa ma trận `position_permission` (động theo chức danh → đổi home/sidebar nhóm đó) | Nghiệp vụ theo permission của job_pos |
| Việc cấm | Nghiệp vụ doanh nghiệp (emp/công/lương…) | Quản trị hệ thống / sửa ma trận |

**Liên module (emp → admin):** HR tạo employee → notification cho ADMIN → tạo `sys_user` theo mã nhân viên (trigger/notification thuộc module emp; tạo account thuộc sys-user). Chi tiết khi viết module docs.

---

## 3. Module MVP (tóm tắt)

| Module | MVP | Ghi chú |
|--------|-----|---------|
| Filter + session + PositionPermissionMatrix | ✅ #1 | Tách realm |
| Admin: sys-user + permission-matrix | ✅ | Không nghiệp vụ |
| Employee/contract (USER) | ✅ tối thiểu | + notification khi tạo emp |
| Attendance / payslip | ✅ tối thiểu | Theo DataScope job_pos |
| Leave/OT/promotion/resignation | ⛔ | Treo |
| Bảng menu | ⛔ | Không làm — hiểu nhầm đã sửa |

---

## 4. Việc còn lại

1. Bạn mô tả chi tiết màn hình/module ADMIN và từng nhóm job_pos (USER).
2. Viết module docs theo tmpl.
3. Refactor code theo Filter 2 realm.
