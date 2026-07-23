# 03 — Người phụ thuộc (Dependent)

**Topic:** `org` / dependent  
**Status:** Draft for Review (rev 0.3)  
**Date:** 2026-07-23  
**Catalogue:** [00-uc-catalog.md](./00-uc-catalog.md)

> Không URL / DDL / Controller / pseudocode.

---

## Định nghĩa

NPT gắn emp; **ACTIVE | INACTIVE**; không duyệt. Payroll đếm NPT ACTIVE.

---

## UC-DEP-01 — Self tạo / sửa / INACTIVE

| Mục | Nội dung |
|-----|----------|
| Actor | USER — SELF |
| BR | Field tối thiểu (Spec); coi đăng ký đúng mặc định |

---

## UC-DEP-02 — HRS xem & INACTIVE

| Mục | Nội dung |
|-----|----------|
| Actor | HRS (ALL) |
| BR | Số + danh sách trên ngữ cảnh emp; INACTIVE nếu sai |

---

## Liên kết

Bù TK / status tài khoản: [../admin/02-sys-user-provision.md](../admin/02-sys-user-provision.md).  
Notification: **treo** — không thuộc Core MVP wave này.

---

## Change log

| Ver | Date | Note |
|-----|------|------|
| 0.3.0 | 2026-07-23 | Bỏ trỏ notify bắt buộc |
