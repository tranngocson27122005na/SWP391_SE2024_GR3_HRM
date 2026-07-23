# 02 — Use-case Hợp đồng lao động (Labor contract)

**Topic:** `org` / contract  
**Status:** Draft for Review (rev 0.3)  
**Date:** 2026-07-23  
**Catalogue:** [00-uc-catalog.md](./00-uc-catalog.md)

> Không URL / DDL / Controller / pseudocode. Happy path; org không ghi `sys_user`.

---

## Định nghĩa

- Nhiều HĐ / emp; tối đa một **ACTIVE**; tham chiếu hiện hành nullable.  
- Loại MVP: **Thử việc | Chính thức** (thử việc → ×0.85 khi tính lương).  
- Trạng thái: **ACTIVE | INACTIVE**.

---

## UC-CON-01 — Xem HĐ

| Mục | Nội dung |
|-----|----------|
| Actor | `contract:READ` + DataScope |
| BR | FAC-SUP thấy lương phòng — ADR-0003 |

---

## UC-CON-02 — Tạo / switch

| Mục | Nội dung |
|-----|----------|
| Actor | `contract:CREATE` |
| BR | HĐ mới ACTIVE; cũ → INACTIVE; cập nhật tham chiếu; **không** đụng TK |

---

## UC-CON-03 — Ngừng HĐ (`contract:DELETE` → INACTIVE)

| Mục | Nội dung |
|-----|----------|
| Actor | `contract:DELETE` |
| BR — lịch sử | Chỉ INACTIVE bản ghi |
| BR — HĐ hiện hành | INACTIVE; tham chiếu null; emp = đã nghỉ |
| `sys_user` | **Không** auto, **không** notify. ADMIN đưa vào hàng đợi offboard và xử lý trong **BR-ADM-SYNC-01** (mỗi ngày làm việc) — ADR-0004. |

---

## UC-CON-04 — Kích hoạt lại HĐ

| Mục | Nội dung |
|-----|----------|
| Actor | `contract:UPDATE` |
| BR | HĐ → ACTIVE; HĐ ACTIVE khác → INACTIVE; cập nhật tham chiếu |
| `sys_user` | Org không ghi. ADMIN Active lại TK theo kiểm tra định kỳ / khi cần (ADR-0004). |

---

## Payroll (đọc)

Một HĐ ACTIVE; loại → ×0.85; mức + kiểu lương; không HĐ ACTIVE → không tính lương NV đó.

---

## Permission

`contract:READ` | `CREATE` | `UPDATE` | `DELETE`

---

## Change log

| Ver | Date | Note |
|-----|------|------|
| 0.3.0 | 2026-07-23 | Bỏ notify/auto TK; chỉ nghiệp vụ HĐ |
| 0.4.0 | 2026-07-23 | Trỏ BR-ADM-SYNC-01 / ADR-0004 |
