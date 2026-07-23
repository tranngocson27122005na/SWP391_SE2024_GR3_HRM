# 01-specification.md — Payroll

**Module:** Payroll  
**Domain:** `payroll`  
**Feature(s):** `payslip`  
**Discovery:** `docs/discovery/payroll/00-uc-catalog.md` + Layer 2/3 v2  
**Version:** 1.0.0 | **Date:** 2026-07-23 | **Status:** Draft

## 1. Context
Sửa param → tính lương từ summary + HĐ + NPT → bảng nháp DRAFT.

## 2. Scope
**In:** param UI; run calculate; list/detail nháp.  
**Out:** finalize, export FINAL, RESP/POS, override dòng.

## 3. Actors
HR-MGR / HR-STF — ALL. FAC-WRK không xem DRAFT.

## 4. Permissions
| Permission | Việc |
| --- | --- |
| `payslip:UPDATE` | Sửa param (seed mới nếu thiếu) |
| `payslip:CREATE` | Nút tính lương |
| `payslip:READ` | Xem nháp |

## 5. FR
| FR | Mô tả | Perm |
| --- | --- | --- |
| FR-P01 | List/edit param | UPDATE |
| FR-P02 | Tính lương kỳ đã import | CREATE |
| FR-P03 | List/detail nháp | READ |

## 6. BR (tóm tắt Layer 3)
- Chỉ NV có `current_contract_id`.
- OFFICE unpaid = 0.
- Meal = OT_weekday × OT_SALARY_WF; threshold param.
- OT WE/HOL × Hourly × rate; MONTHLY → `/ TotalWorkDays / 8`.
- Snapshot param theo batch; replace nháp khi tính lại.

## 7. Validation
Kỳ có summary; đủ param bắt buộc; file không — N/A trên run.
