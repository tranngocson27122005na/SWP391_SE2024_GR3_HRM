# 03-design.md — Payroll

| Method | URL | Permission |
| --- | --- | --- |
| GET | `/payslip/list` | READ |
| GET | `/payslip/detail` | READ |
| GET+POST | `/payslip/edit` + `/payslip/update` | UPDATE (param) |
| GET+POST | `/payslip/create` | CREATE (chọn kỳ + run) |

Package: `controller.payroll` / `service.payroll` (+ `PayrollEngine`).  
AuthFilter: `/payslip/`.
