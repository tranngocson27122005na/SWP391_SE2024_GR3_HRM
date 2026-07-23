# 04-implementation.md — Attendance

**Version:** 1.0.0

1. Migration `1.attendance_payroll_mvp.sql` (chung payroll).
2. Entity/mapper summary mở rộng; CSV parser (header đúng catalog).
3. AuthFilter prefix `/attendance/`; Home tile nếu IMPORT hoặc READ.
4. Import TX: delete payroll results → upsert summaries.

Smoke: import mẫu `docs/discovery/payroll/samples/attendance_summary_import_template.csv`.
