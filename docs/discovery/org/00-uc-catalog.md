# 00 — Catalogue use-case: Hồ sơ & Vòng đời theo Hợp đồng

**Topic:** `org` (Employee lifecycle via Contract)  
**Status:** Draft for Review (rev 0.4)  
**Date:** 2026-07-23  
**Nguồn tham khảo (Locked — chọn lọc):** Layer 0 §1; Layer 1 §1.1–1.4; NQLD Ch.II Điều 3; Layer 3 (payroll đọc)  
**Phụ thuộc:** `docs/discovery/auth/01-login-authorization.md`; `sys_user` → admin; **ADR-0004**  
**Mục đích:** Chốt **happy path Core MVP** phục vụ tính lương sau — trước Spec `docs/modules/org/`.

> Discovery **không** URL / DDL / Controller / pseudocode.  
> Liên kết TK: thao tác tay + kiểm tra định kỳ có tần suất — không trigger/notify (ADR-0004).

---

## 0. Quyết định provision & liên kết TK (rev 0.4 — ADR-0004)

| Đã xét | Kết luận MVP |
|--------|----------------|
| Org tạo / khóa `sys_user` | **Không** — sai realm |
| DB trigger / notification (onboard & offboard) | **Lược bỏ có chủ đích** — ưu tiên chính xác/đơn giản (ADR-0004) |
| ADMIN bù TK + đổi status tay | **Có** — kèm **BR kiểm tra mỗi ngày làm việc** (không tùy hứng) |

Đây là **đánh đổi có chủ đích**, không phải quên tự động hóa. Điều kiện mở lại: ADR-0004 § Revisit. Chi tiết BR: [`../admin/02-sys-user-provision.md`](../admin/02-sys-user-provision.md).

---

## 1. Bài toán

HRS quản hồ sơ + HĐ; đúng **một HĐ ACTIVE** / emp; emp = thông tin; đang làm / nghỉ neo theo HĐ.  
Input payroll: loại HĐ (×0.85 thử việc); mức + kiểu lương; NPT ACTIVE; `employment_group`.

---

## 2. Triết lý MVP

- Happy path trước; Locked docs = tham khảo chọn lọc.  
- Trạng thái: **ACTIVE / INACTIVE**.  
- Không `employee.status` cho vòng đời; không `employee:DELETE`.  
- Org **không** ghi `sys_user`. Không trigger. Không notification bắt buộc (onboard **và** offboard).  
- ADMIN đồng bộ tay theo **BR-ADM-SYNC-01: mỗi ngày làm việc** (ADR-0004).  
- Domain `org`: `employee`, `contract`, `dependent`.

---

## 3. Actor & DataScope

| Actor | Realm | DataScope | Việc |
|-------|-------|-----------|------|
| HR-MGR / HR-STF | USER | ALL | CUD hồ sơ+HĐ; NPT; list emp (đang làm + đã nghỉ) |
| FAC-SUP | USER | DEPARTMENT | Xem emp+HĐ phòng (kể cả lương — ADR-0003) |
| Nhân sự khác | USER | SELF | Self-profile dùng chung; HĐ/NPT mình |
| ADMIN | ADMIN | — | `sys_user`; bù TK / đổi status; **kiểm tra mỗi ngày làm việc** (BR-ADM-SYNC-01) |

---

## 4. Catalogue (org)

| ID | Use-case | File |
|----|----------|------|
| UC-EMP-01…05 | List / detail / self / tạo emp+HĐ / sửa hồ sơ | [01](./01-employee-profile.md) |
| UC-CON-01…04 | Xem / tạo-switch / DELETE→INACTIVE / activate HĐ | [02](./02-labor-contract.md) |
| UC-DEP-01/02 | NPT | [03](./03-dependent.md) |

**Ngoài org:** [admin bù TK](../admin/02-sys-user-provision.md).  
**Treo wave này:** notification in-app; DB trigger provision; auto khóa TK sau kỳ lương; mail; pro-rate giữa kỳ; duyệt NPT.

---

## 5. Permission (ma trận)

| Permission | Gán tối thiểu |
|------------|---------------|
| `employee:READ` | HR-MGR, HR-STF, FAC-SUP |
| `employee:CREATE` / `UPDATE` | HR-MGR, HR-STF |
| `contract:READ` | HR-MGR, HR-STF, FAC-SUP, FAC-WRK |
| `contract:CREATE` / `UPDATE` / `DELETE` | HR-MGR, HR-STF |
| Dependent (tên Spec) | Self + HRS |

Không ma trận: self-profile, login/home/đổi MK.  
Backlog seed: thêm `contract:DELETE`; bỏ gán `employee:DELETE` và hướng `employee-self:READ`.

---

## 6. Quyết định khóa (rev 0.3)

1. Một HĐ ACTIVE / emp; tham chiếu hiện hành nullable.  
2. Switch/tạo mới: HĐ cũ INACTIVE; không đụng `sys_user`.  
3. DELETE HĐ hiện hành: null tham chiếu; **không** auto/notify khóa TK — ADMIN xử lý trong kiểm tra **mỗi ngày làm việc** (BR-ADM-SYNC-01).  
4. Activate HĐ: chỉ đổi HĐ + tham chiếu; bật lại TK = ADMIN tay (cùng kỷ luật kiểm tra).  
5. Tạo NV: TX org = emp + HĐ. TK = ADMIN bù tay; **không** trigger/notify (ADR-0004).  
6. ADMIN được override Active TK dù không HĐ ACTIVE (vẫn phải thấy trong hàng đợi offboard).

---

## 7. Phụ thuộc

```
common-auth (login, home, self-profile whitelist)
    → admin (sys_user, ma trận, bù TK tay)
    → org (employee, contract, dependent)
         → payroll (đọc)
```

---

## 8. Checklist

- [ ] Duyệt ADR-0004 + BR-ADM-SYNC-01 (tần suất mỗi ngày làm việc)  
- [ ] Duyệt lược bỏ trigger + notification hai chiều  
- [ ] Sau duyệt → Spec org (+ Spec admin bù TK / hàng đợi kiểm tra)

---

## 9. Change log

| Ver | Date | Note |
|-----|------|------|
| 0.1–0.2 | 2026-07-23 | Draft + đối chất realm |
| 0.3.0 | 2026-07-23 | Không trigger/notify; ADMIN bù tay |
| 0.4.0 | 2026-07-23 | ADR-0004; BR kiểm tra mỗi ngày làm việc |
