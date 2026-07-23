# 02-database.md — Attendance

**Module:** Attendance | **Version:** 1.0.0 | **Migration:** `sql/1.attendance_payroll_mvp.sql`

## Changes vs `0.hrmdb` `attendance_summary`

| Column | Type | Notes |
| --- | --- | --- |
| import_id | INT NULL FK | |
| period_month | TINYINT NOT NULL | 1–12 |
| period_year | SMALLINT NOT NULL | |
| total_work_days | DECIMAL | **Ngày phải làm** (đổi nghĩa) |
| actual_work_days | DECIMAL | Ngày thực tế |
| paid_leave_days / unpaid_leave_days / holiday_days | DECIMAL | |
| ot_weekday_hours / ot_weekend_hours / ot_holiday_hours | DECIMAL | |
| late_early_blocks | INT | |
| UNIQUE | (employee_id, period_year, period_month) | thay uk cũ nếu cần |

Giữ cột cũ không dùng (total_ot_hours…) nullable hoặc bỏ trong migration nếu an toàn.  
`daily_attendance` **không đổi**.
