# 00 — Catalogue use-case: Attendance (import summary)

**Topic:** `attendance`  
**Status:** Draft for Review (rev 1.0)  
**Date:** 2026-07-23  
**Phạm vi CoreMVP:** **chỉ** import + lưu/xem `attendance_summary` (tổng hợp tháng).  
**Không làm:** daily In/Out, khóa công, leave/OT request, self-service chấm công đầy đủ.  
**Nguồn:** Layer 1–3 v2; chốt BA + [`../payroll/00-uc-catalog.md`](../payroll/00-uc-catalog.md)  
**Phụ thuộc:** org (`employee_code`, `employment_group`)  
**Mẫu CSV:** [`../payroll/samples/attendance_summary_import_template.csv`](../payroll/samples/attendance_summary_import_template.csv)

> Discovery **không** URL / DDL chi tiết / Controller / pseudocode.

---

## 1. Bài toán

HRS chuẩn bị CSV tổng hợp công tháng → import → hệ thống khớp `EmployeeCode` → upsert `attendance_summary` theo NV + kỳ → phục vụ payroll run.

---

## 2. Triết lý MVP

| Chọn | Kết luận |
|------|----------|
| Đơn vị dữ liệu | **1 dòng / NV / tháng** |
| Daily `daily_attendance` | Giữ schema tượng trưng; **không** dùng |
| Khóa công | **Không** (payroll 2A) |
| FullName / Position trên CSV | **Không** — suy từ emp |
| Ngày trên lịch Day1…31 | **Không** import |

---

## 3. Actor

| Actor | Việc |
|-------|------|
| HR-MGR / HR-STF | Import CSV; xem summary kỳ |
| Khác | Không vận hành import CoreMVP |

Permission: `attendance:IMPORT`, `attendance:READ` (đã seed).

---

## 4. Catalogue UC

| ID | Use-case | Mô tả |
|----|----------|--------|
| UC-ATT-01 | Import summary | Upload CSV → validate → ghi import log + upsert summary; **side-effect payroll:** xóa toàn bộ kết quả payroll cũ (xem payroll catalog) |
| UC-ATT-02 | Xem summary | List/filter theo tháng–năm; theo DataScope nếu Spec mở |

---

## 5. Cột CSV (đồng bộ Layer 3)

| Cột | Ý nghĩa | Cột summary (logical) |
|-----|---------|------------------------|
| `EmployeeCode` | Mã NV | → employee |
| `TotalWorkDays` | Ngày công **phải làm** (Excel tạm: VP 22, xưởng 26) | `total_work_days` |
| `ActualWorkDays` | Ngày công **thực tế** | `actual_work_days` |
| `TotalPaidLeave` | Nghỉ có phép | `paid_leave_days` |
| `TotalUnpaidLeave` | Nghỉ không phép | `unpaid_leave_days` |
| `TotalHoliday` | Ngày lễ | `holiday_days` |
| `OT_Weekday_Hours` | OT ngày thường | `ot_weekday_hours` |
| `OT_Weekend_Hours` | OT cuối tuần | `ot_weekend_hours` |
| `OT_Holiday_Hours` | OT lễ | `ot_holiday_hours` |
| `Late_Early_Blocks` | Block muộn/sớm | `late_early_blocks` |

Kỳ tháng/năm: chọn trên UI lúc import (hoặc Spec thêm cột sau).

---

## 6. Business rules import

| ID | Rule |
|----|------|
| BR-ATT-01 | Không khớp `EmployeeCode` → bỏ dòng + báo cáo (không abort cả file trừ Spec siết) |
| BR-ATT-02 | Upsert theo `(employee_id, period_month, period_year)` |
| BR-ATT-03 | **OFFICE:** ép / coi `TotalUnpaidLeave = 0` khi import hoặc khi payroll đọc (CoreMVP: văn phòng không dùng nghỉ không phép) |
| BR-ATT-04 | Import kỳ mới → xóa kết quả **payroll** (không xóa org; summary theo upsert) |
| BR-ATT-05 | Tên cột CSV MUST khớp bảng §5 (`Late_Early_Blocks` đúng underscore) |

---

## 7. Ngoài phạm vi (treo)

Daily punch, attendance request, lock summary, leave module, đồng bộ `leave_balance` bắt buộc.

---

## 8. Trạng thái

| Bước | Status |
|------|--------|
| Discovery attendance import | **Draft 1.0 — sẵn sàng Spec** |
| Spec `docs/modules/attendance/` | Tiếp theo |
| Code | Sau Spec |
