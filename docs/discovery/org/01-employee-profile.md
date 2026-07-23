# 01 — Use-case Hồ sơ nhân viên (Employee profile)

**Topic:** `org` / employee  
**Status:** Draft for Review (rev 0.3)  
**Date:** 2026-07-23  
**Catalogue:** [00-uc-catalog.md](./00-uc-catalog.md)

> Không URL / DDL / Controller / pseudocode. Happy path Core MVP.

---

## Định nghĩa

- Emp = hồ sơ thông tin; không dùng status emp cho vòng đời.  
- Đang làm ⇔ có HĐ ACTIVE. Đã nghỉ ⇔ không. List gồm cả hai (trong DataScope).

---

## UC-EMP-01 — List

| Mục | Nội dung |
|-----|----------|
| Actor | `employee:READ` |
| BR | DataScope trước phân trang; keyword mã/họ tên |

---

## UC-EMP-02 — Detail trong scope

| Mục | Nội dung |
|-----|----------|
| Actor | `employee:READ` |
| BR | Ngoài scope → từ chối; HRS thấy số NPT |

---

## UC-EMP-03 — Hồ sơ mình (dùng chung)

| Mục | Nội dung |
|-----|----------|
| Actor | Mọi USER login có `employeeId` |
| Phân quyền | Whitelist common-auth — **không** ma trận; **không** `employee-self:READ` |
| BR | Chỉ emp của phiên |

---

## UC-EMP-04 — Tạo NV + HĐ đầu (chỉ org)

| Mục | Nội dung |
|-----|----------|
| Actor | HRS (`employee:CREATE` + `contract:CREATE`) |
| Transaction org | Emp + HĐ ACTIVE + tham chiếu hiện hành. **Không** `sys_user`. |
| Tài khoản | **Không** tạo ở đây. ADMIN bù tay theo **BR-ADM-SYNC-01** ([admin](../admin/02-sys-user-provision.md), ADR-0004). |
| Out | Trigger; notification; org tạo TK |

---

## UC-EMP-05 — Sửa hồ sơ

| Mục | Nội dung |
|-----|----------|
| Actor | `employee:UPDATE` |
| BR | Không đổi vòng đời / mã NV (đề xuất khóa); không DELETE emp |

---

## Permission

`employee:READ` | `CREATE` | `UPDATE` — không DELETE / employee-self.

---

## Change log

| Ver | Date | Note |
|-----|------|------|
| 0.3.0 | 2026-07-23 | Bỏ khung A/B trigger/notify; chỉ bù tay |
