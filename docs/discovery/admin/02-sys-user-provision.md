# 02 — Sys-user: bù TK tay + kiểm tra định kỳ (admin — Core MVP)

**Topic:** `admin` / sys_user  
**Status:** Draft for Review (rev 0.4)  
**Date:** 2026-07-23  
**ADR:** [ADR-0004](../../decisions/ADR-0004-manual-sys-user-sync-periodic-check.md)  
**Phụ thuộc:** common-auth realm; org tạo emp+HĐ (không tạo TK)  
**Spec chính:** `docs/modules/admin/` (bổ sung khi implement)

> **Đánh đổi có chủ đích (ADR-0004):** không trigger, không notify (onboard **và** offboard). Ưu tiên chính xác/đơn giản. ADMIN **bắt buộc** kiểm tra định kỳ theo BR dưới — không phải “khi nhớ”.

---

## Business rules (tường minh)

### BR-ADM-SYNC-01 — Kiểm tra định kỳ (bắt buộc)

| Mục | Nội dung |
|-----|----------|
| Ai | Tài khoản **ADMIN** (vận hành hệ thống) |
| Tần suất | **Mỗi ngày làm việc, đúng một lần tối thiểu** (T2–T6 theo lịch công ty; ngày nghỉ lễ không bắt buộc) |
| Việc phải làm | Mở màn quản trị liên quan và xử lý hết mục lệch còn mở (xem hai hàng đợi dưới) |
| Hàng đợi onboard | Emp đã có hồ sơ (+ thường đã có HĐ) nhưng **chưa có** `sys_user` → bù TK (UC-ADM-SYS-02) hoặc ghi nhận cố ý chưa cấp |
| Hàng đợi offboard | `sys_user` **ACTIVE** mà emp **không còn** HĐ ACTIVE → **INACTIVE** TK (UC-ADM-SYS-01), trừ khi cố ý override (ghi nhận) |
| Không đạt | Bỏ qua kiểm tra trong ≥ 1 ngày làm việc = vi phạm quy trình vận hành MVP (không phải “tùy chọn”) |

### BR-ADM-SYNC-02 — Phạm vi hai chiều

- **Onboard:** không auto, không notify; chỉ tay + BR-ADM-SYNC-01.  
- **Offboard:** không auto, không notify; chỉ tay + BR-ADM-SYNC-01.  
- Cùng một kỷ luật kiểm tra cho cả hai chiều (ADR-0004).

### BR-ADM-SYNC-03 — Override

ADMIN vẫn được Active TK dù emp không HĐ ACTIVE (ngoại lệ có chủ đích). Override **không** miễn trừ việc phải **thấy** bản ghi trong hàng đợi offboard và quyết định có chủ đích (giữ Active hoặc Inactive).

---

## UC-ADM-SYS-01 — List / đổi status

- ACTIVE / INACTIVE nhanh.  
- Dùng trong hàng đợi offboard (BR-ADM-SYNC-01).

---

## UC-ADM-SYS-02 — Bù tạo TK

| Mục | Nội dung |
|-----|----------|
| Mục đích | Cấp `sys_user` cho emp chưa có TK |
| Actor | ADMIN |
| BR | Username = mã NV; MK mặc định MVP; role USER; gắn `employee_id`. List “emp chưa gắn TK” là đầu vào hàng đợi onboard. |

---

## Luồng vận hành (happy path + kỷ luật)

1. HRS: tạo/sửa emp+HĐ (org).  
2. **Mỗi ngày làm việc:** ADMIN chạy BR-ADM-SYNC-01 (onboard + offboard).  
3. Không phụ thuộc notification hay trigger.

---

## Treo

Trigger; notification; auto khóa sau kỳ lương — chỉ mở lại khi đủ điều kiện **Revisit** trong ADR-0004.

---

## Change log

| Ver | Date | Note |
|-----|------|------|
| 0.3.0 | 2026-07-23 | Chốt bù tay |
| 0.4.0 | 2026-07-23 | BR tần suất mỗi ngày làm việc; ADR-0004 |
