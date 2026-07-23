# 01-specification.md — Attendance (import summary)

**Module:** Attendance  
**Domain:** `attendance`  
**Feature(s):** `attendance`  
**Discovery:** `docs/discovery/attendance/00-uc-catalog.md` rev 1.0  
**Version:** 1.0.0 | **Date:** 2026-07-23 | **Status:** Draft

## 1. Context
Import CSV tổng hợp công tháng → `attendance_summary` làm input payroll.

## 2. Scope
**In:** import CSV; list summary theo kỳ.  
**Out:** daily punch; khóa công; leave/OT request.

## 3. Actors
| Actor | DataScope | Việc |
| --- | --- | --- |
| HR-MGR / HR-STF | ALL | Import + xem |
| ADMIN | — | 403 URL nghiệp vụ |

## 4. Permissions
| Permission | Gán |
| --- | --- |
| `attendance:IMPORT` | HR-MGR, HR-STF (đã seed) |
| `attendance:READ` | HR-MGR, HR-STF (+ FAC-SUP nếu đã seed) |

## 5. FR
| FR | Mô tả | Permission | Acceptance |
| --- | --- | --- | --- |
| FR-A01 | Import CSV | IMPORT | Upsert summary; báo cáo dòng lỗi; xóa payroll cũ |
| FR-A02 | List summary | READ | Lọc tháng/năm |

## 6. BR
- BR-01: Cột CSV đúng catalog (gồm `Late_Early_Blocks`).
- BR-02: OFFICE → `unpaid_leave_days = 0`.
- BR-03: Không khớp mã NV → skip + report.
- BR-04: Import → xóa toàn bộ kết quả payroll (không xóa org).

## 7. Validation (form)
| Field | Rule | Message |
| --- | --- | --- |
| file | required, .csv | "Chọn file CSV" |
| periodMonth/Year | required | "Chọn kỳ lương" |
