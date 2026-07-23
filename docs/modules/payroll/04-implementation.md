# 04-implementation.md — Payroll

1. Cùng migration attendance.  
2. Seed `payslip:UPDATE` nếu thiếu; gán HR-MGR/HR-STF.  
3. Engine atomic theo Layer 3 v2.  
4. Home tiles: Chấm công / Tính lương.

**Trước smoke:** user chạy `sql/1.attendance_payroll_mvp.sql` trên `hrmdb`.
