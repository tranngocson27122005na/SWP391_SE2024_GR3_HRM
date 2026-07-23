## METADATA

* Artifact_ID: HRMS-L1.5-COMP-ENT-001
* Version: **2.0.0**
* Document_Status: **Draft for Review** (thay bản 1.1.0 Locked — thiếu tham số hóa / suy luận cấm khiến lệch CoreMVP)
* Document_Title: Entity Mapping — HRMS Payroll inputs (CoreMVP)
* Dependency_List: org module; Layer 2; Layer 3; `docs/discovery/payroll/00-uc-catalog.md`
* Change_Log: v2.0.0 2026-07-23 — Tham số hóa; attendance summary; OT weekday → meal; bỏ RESP/POS MVP

### Nguyên tắc chung

* Mọi **số tiền / tỷ lệ / ngày công chuẩn / trần thuế / đơn giá giờ** MUST là **tham số** (DB + UI HRS sửa được) — **MUST NOT** hardcode trong code.
* Giá trị trong tài liệu chỉ là **seed / ví dụ mặc định**.
* Org đã chốt: `employment_group`, `current_contract_id`, loại HĐ PROBATION|OFFICIAL, NPT ACTIVE.

---

## 1. Employee & Contract (đọc từ org)

### 1.1 Employee Profile

* Định danh: mã NV, họ tên, ngày sinh, phòng ban/chức vụ, TK ngân hàng, `joining_date`
* `Employee_Group` / `employment_group`: **OFFICE | FACTORY** — bắt buộc; phân nhánh Layer 3

### 1.2 Dependents

* NPT ACTIVE → `Number_of_Dependents` cho PIT (Layer 3)
* Field: họ tên, quan hệ, MST, start/end

### 1.3 Labor Contract

* MVP loại: **PROBATION | OFFICIAL**
* `Is_Probation` ⇔ loại PROBATION
* `Contract_Salary` = `basic_salary`
* `salary_type`: **MONTHLY | HOURLY** (đã có trên org/contract)
  * **HOURLY:** `Contract_Salary` = đơn giá giờ thỏa thuận — dùng trực tiếp khi cần `Hourly_Salary`
  * **MONTHLY:** khi cần giờ:  
    `Hourly_Salary = Contract_Salary / TotalWorkDays / HOURS_PER_WORK_DAY`  
    (`TotalWorkDays` từ attendance_summary / CSV — VP thường 22, xưởng 26 trên Excel)
* **MUST** có `current_contract_id` mới payroll; không có → skip NV

### 1.4 Onboarding / Offboarding

* Đang làm ⇔ có HĐ hiện hành; đã nghỉ ⇔ null current
* Provision `sys_user`: ngoài org (ADR-0004) — Layer 1 không mô tả trigger

---

## 2. Work schedule & Attendance (CoreMVP)

### 2.1 Work Schedule (tham số)

* `WORKING_DAYS_IN_MONTH_OFFICE` — mặc định ví dụ **22** (T2–T6)
* `WORKING_DAYS_IN_MONTH_FACTORY` — mặc định ví dụ **26** (T2–T7)
* Engine chọn param theo `Employee_Group` → biến `Working_Days_In_Month`
* **MUST NOT** hardcode 26 trong công thức

### 2.2 Attendance Summary (input payroll)

* CoreMVP: import **CSV tổng hợp tháng** (không Day1…31; không bắt buộc daily In/Out)
* Một dòng / NV / kỳ (`PeriodMonth`, `PeriodYear`)
* Field tối thiểu đưa vào engine / CSV:

| Field CSV | DB summary | Ý nghĩa |
|-------|---------|---------|
| `EmployeeCode` | → employee | Mã NV |
| `TotalWorkDays` | `total_work_days` | Ngày công **phải làm** (Excel tạm 22/26) |
| `ActualWorkDays` | `actual_work_days` | Ngày công **thực tế** |
| `TotalPaidLeave` / `TotalUnpaidLeave` / `TotalHoliday` | `paid_leave_days` / … | Nghỉ / lễ |
| `OT_Weekday_Hours` / `OT_Weekend_Hours` / `OT_Holiday_Hours` | `ot_*_hours` | OT 3 loại |
| `Late_Early_Blocks` | `late_early_blocks` | Block muộn/sớm |

* **Không** import FullName/Position — join từ `EmployeeCode`
* `Working_Days_In_Month` (engine) := `total_work_days` trên summary
* `Actual_Working_Hours` (FACTORY) = `actual_work_days × HOURS_PER_WORK_DAY`
* **OFFICE — nghỉ không phép:** CoreMVP MUST coi `TotalUnpaidLeave` / `unpaid_leave_days` = **0**
### 2.3 Leave

* Số ngày nghỉ trên summary CSV đủ cho CoreMVP
* Leave Request / duyệt 2 cấp: **treo**

### 2.4 Insufficient hours deduction

* Block size + rate = **param tĩnh** (Layer 2/3); công thức Layer 3
* Không phải kỷ luật

---

## 3. Overtime

### 3.1 Nguồn giờ

* Giờ OT từ **attendance summary CSV** (không OT Request module CoreMVP)

### 3.2 Phân loại & cách trả (CoreMVP)

| Loại | Ai | Cách trả |
|------|-----|----------|
| **Weekday** | Chỉ FACTORY (OFFICE = 0 giờ hiệu lực) | **Không** nhân hệ số 1.5 lên lương giờ. Đơn giá giờ OT weekday = param **`OT_SALARY_WF`** (VND/giờ). `Total_Meal_Allowance = OT_Weekday_Hours × OT_SALARY_WF` → tách thuế theo trần (Layer 2/3) |
| **Weekend** | ALL (kể cả OFFICE) | Giờ CSV × **`Hourly_Salary`** × `OT_RATE_WEEKEND`. `Hourly_Salary` suy từ HĐ (§1.3): HOURLY dùng thẳng; MONTHLY = `basic_salary / Working_Days_In_Month / HOURS_PER_WORK_DAY` (OFFICE thường /22/8 — 22 cấu hình được) |
| **Holiday** | ALL (kể cả OFFICE) | Tương tự × `OT_RATE_HOLIDAY` |

* Hệ số / đơn giá: **param động trong DB**, HRS sửa UI — MUST NOT hardcode 15000 / 2.0 / 3.0 trong code

### 3.3 Bonus holiday riêng

* Không làm (chưa chính sách)

---

## 4. Đãi ngộ (CoreMVP)

| Khoản | Config |
|-------|--------|
| Phép năm theo khối | Param / treo module leave |
| Thâm niên (tiền) | Tính Layer 3 từ `joining_date` + param ngưỡng/% — **không** chờ Layer 4 |
| RESP / POS | **Out of CoreMVP** (chưa chốt) |
| Severance | Out of CoreMVP |
| Ăn ca | Suy từ OT weekday × `OT_SALARY_WF` (mục 3.2) |

---

## 5. Payroll entities (ánh xạ Layer 2)

* **Earning:** BASE / PROBATION, SENIORITY, MEAL_FIXED / MEAL_SURP, OT_WE, OT_HOL  
* **Statutory:** SOCIAL_INS (gộp hoặc tách), PIT  
* **Internal deduction:** TIME_DEDUCT  
* **Net Pay:** công thức chỉ ở Layer 3  
* **Payslip / batch nháp:** HRS xem DRAFT; snapshot param theo lần chạy

---

## 6. Liên kết org

* Payroll **đọc** org; không sửa vòng đời HĐ khi tính lương  
* Import kỳ mới: xóa kết quả payroll cũ (không xóa org) — chi tiết `00-uc-catalog`
