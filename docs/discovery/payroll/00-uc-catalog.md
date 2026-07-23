# 00 — Catalogue use-case: Payroll (param + tính nháp)

**Topic:** `payroll` (resource permission: `payslip`)  
**Status:** Draft for Review (rev 1.0)  
**Date:** 2026-07-23  
**Phụ thuộc:** [`../attendance/00-uc-catalog.md`](../attendance/00-uc-catalog.md); org (HĐ hiện hành, NPT, group); Layer 1–3 **v2**  
**Quyết định BA:** 1A (sửa param, không override dòng phiếu) · 2A (nháp DRAFT, không khóa công / finalize)

> Discovery **không** URL / DDL chi tiết / Controller / pseudocode.  
> Công thức chi tiết: Layer 3. Element/param seed: Layer 2.

---

## 1. Bài toán

HRS (sau khi có `attendance_summary`) chỉnh tham số nếu cần → bấm **Tính lương** → engine Layer 3 → bảng lương **nháp** (batch + dòng element). Công thức **tham số hóa** — không hardcode số trong code.

---

## 2. Triết lý MVP

| Chọn | Kết luận |
|------|----------|
| Input công | Chỉ **attendance_summary** |
| Emp không `current_contract_id` | **Skip** + báo cáo |
| Override amount trên phiếu | **Không** |
| Finalize / duyệt CEO / export FINAL | **Treo** |
| RESP / POS | **Out** |
| Import CSV | Xóa **toàn bộ** kết quả payroll cũ; tính lại = **replace** nháp |

---

## 3. Actor & permission

| Actor | Việc | Permission |
|-------|------|------------|
| HR-MGR / HR-STF | Sửa param; tính lương; xem nháp | `payslip:UPDATE`, `payslip:CREATE`, `payslip:READ` |
| FAC-WRK | Không xem DRAFT wave này | — |

---

## 4. Catalogue UC

| ID | Use-case | Mô tả |
|----|----------|--------|
| UC-PAY-01 | Tham số | Xem/sửa static + dynamic (Layer 2/3) |
| UC-PAY-02 | Tính lương | Snapshot param → DRAFT; chỉ NV có HĐ hiện hành |
| UC-PAY-03 | Xem nháp | List NV + chi tiết element; không sửa tay amount |

---

## 5. Input từ summary (nhắc)

`TotalWorkDays` (phải làm), `ActualWorkDays` (thực tế), phép, OT 3 loại, `Late_Early_Blocks`.  
Chi tiết cột: attendance catalog §5.

### Nghỉ phép CoreMVP

| Group | Paid leave | Unpaid leave |
|-------|------------|--------------|
| **OFFICE** | Đọc CSV nếu có | **Luôn 0** (không dùng nghỉ không phép) |
| **FACTORY** | Đọc CSV | Đọc CSV |

---

## 6. Rule nghiệp vụ gắn BA (tóm tắt — chi tiết Layer 3)

| Chủ đề | Rule |
|--------|------|
| BASE | OFFICE: lương tháng HĐ; FACTORY: theo `salary_type` + `ActualWorkDays × 8` giờ |
| PROB | × `PROBATION_RATE` (seed 0.85) |
| SENIOR | Từ `joining_date`; chưa đủ năm → 0 |
| Ăn ca | `Total_Meal_Allowance = OT_Weekday_Hours × OT_SALARY_WF` (FACTORY); tách `MEAL_ALLOWANCE_THRESHOLD` |
| OT WE/HOL | Giờ CSV × `Hourly_Salary` × hệ số DB; OFFICE được nhận nếu có giờ |
| Hourly từ MONTHLY | `basic_salary / TotalWorkDays / HOURS_PER_WORK_DAY` |
| TIME_DEDUCT | `Hourly_Salary × Late_Early_Blocks × DEDUCTION_RATE_PER_BLOCK` |
| PIT / BH | Param + NPT ACTIVE |

---

## 7. Happy path

```text
Import summary (ATT) → (tuỳ chọn) sửa param → Tính lương → xem nháp DRAFT
```

---

## 8. Treo

Finalize, EXPORT FINAL, khóa công, RESP/POS, override phiếu, leave/OT request module.

---

## 9. Trạng thái

| Bước | Status |
|------|--------|
| Discovery payroll CoreMVP | **Draft 1.0 — sẵn sàng Spec** |
| Spec `docs/modules/payroll/` (+ attendance) | Tiếp theo |
| Code | Sau Spec |
