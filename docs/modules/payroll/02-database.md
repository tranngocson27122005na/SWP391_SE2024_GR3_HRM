# 02-database.md — Payroll

**Migration:** `sql/1.attendance_payroll_mvp.sql`

## Tables
| Table | Purpose |
| --- | --- |
| `payroll_element` | Catalog element_code |
| `payroll_param` | Param hiện hành (code, value, note) — UI sửa |
| `payroll_batch` | Một lần chạy (kỳ + status DRAFT) |
| `payslip` | 1 NV / batch + contract_id |
| `payslip_detail` | element_id + amount |
| `payroll_batch_param` | Snapshot param theo batch |
| `payroll_batch_pit_bracket` | Snapshot bậc thuế |

Status TINYINT (ADR-0002). Seed elements + params từ Layer 2.
