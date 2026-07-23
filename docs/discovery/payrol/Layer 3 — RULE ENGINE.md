## METADATA

* Artifact_ID: HRMS-L3-PAY-RULE-001
* Version: **2.0.0**
* Document_Status: **Draft for Review**
* Document_Title: RULE ENGINE — atomic + aggregation (tham số hóa)
* Dependency_List: Layer 1 v2; Layer 2 v2; org; `docs/discovery/payroll/00-uc-catalog.md`
* Change_Log: v2.0.0 2026-07-23 — …; v2.0.1 — HOURLY-001: MONTHLY→giờ (/Working_Days/8); OT WE/HOL cho OFFICE

### Nguyên tắc

* Mọi `param` đọc từ DB (snapshot khi run). Giá trị số trong công thức dưới = **ký hiệu param**, không literal cố định trong code.
* Thiếu param bắt buộc → **abort** run với lỗi rõ.

---

## 1. Tham số

### 1.1 Static (công ty — vẫn sửa được trên UI CoreMVP)

| param_id | Seed ví dụ | note |
| --- | --- | --- |
| PROBATION_RATE | 0.85 | Thử việc |
| DEDUCTION_BLOCK_MINUTES | 30 | 1 block |
| DEDUCTION_RATE_PER_BLOCK | 0.5 | Giờ lương / block |
| HOURS_PER_WORK_DAY | 8 | |
| WORKING_DAYS_IN_MONTH_OFFICE | 22 | |
| WORKING_DAYS_IN_MONTH_FACTORY | 26 | |

### 1.2 Dynamic (pháp lý / thị trường)

| param_id | Seed ví dụ | note |
| --- | --- | --- |
| SOCIAL_INS_RATE | 0.105 | NLĐ gộp |
| MEAL_ALLOWANCE_THRESHOLD | 730000 | Trần miễn thuế ăn ca |
| **OT_SALARY_WF** | **15000** | VND/giờ OT weekday → meal |
| OT_RATE_WEEKEND | 2.0 | |
| OT_RATE_HOLIDAY | 3.0 | |
| OT_RATE_WEEKDAY | 1.5 | Không dùng trả weekday CoreMVP |
| PIT_PERSONAL_EXEMPTION | 11000000 | |
| PIT_DEPENDENT_EXEMPTION | 4400000 | |
| PIT_BRACKET_* / rates | theo Layer 2 | |

---

## 2. Atomic rules

### PAY-RULE-BASE-001 — Lương cơ bản theo nhóm

* Input: `Employee_Group`, `Contract_Salary`, `ActualWorkDays`, `HOURS_PER_WORK_DAY`
* `Actual_Working_Hours = ActualWorkDays × HOURS_PER_WORK_DAY` (FACTORY)
* Output: `Calculated_Base_Salary`
* Formula:
  * OFFICE: `Calculated_Base_Salary = Contract_Salary`
  * FACTORY: `Calculated_Base_Salary = Contract_Salary × Actual_Working_Hours`  
    *(Nếu HĐ lưu lương tháng FACTORY thay vì đơn giá giờ — Spec xác nhận đơn vị `Contract_Salary`; giữ tham số hóa.)*

### PAY-RULE-PROB-001 — Thử việc

* Input: `Is_Probation`, `Calculated_Base_Salary`, `PROBATION_RATE`
* Output: `Final_Base_Salary`
* `Is_Probation`: `Final_Base_Salary = Calculated_Base_Salary × PROBATION_RATE`  
  else: `Final_Base_Salary = Calculated_Base_Salary`

### PAY-RULE-SENIOR-001 — Thâm niên

* Input: `Joining_Date`, `Payroll_Period_End`, `Final_Base_Salary`  
  + param ngưỡng năm / % (seed: &lt;5 năm → 0%; ≥5 → min(5% + (years-5)×1%, 100%) — **các hệ số % SHOULD là param** nếu Spec tách)
* Output: `Seniority_Allowance`
* Chưa đủ năm → **0** (rule vẫn chạy)

### PAY-RULE-MEAL-000 — Tạo Total_Meal_Allowance (CoreMVP)

* Input: `Employee_Group`, `OT_Weekday_Hours`, **`OT_SALARY_WF`**
* Output: `Total_Meal_Allowance`
* Formula:
  * OFFICE: `Total_Meal_Allowance = 0`
  * FACTORY: `Total_Meal_Allowance = OT_Weekday_Hours × OT_SALARY_WF`
* **Không** dùng `OT_Weekend_Hours` / `OT_Holiday_Hours` ở rule này

### PAY-RULE-MEAL-001 — Phần không chịu thuế

* Input: `Total_Meal_Allowance`, `MEAL_ALLOWANCE_THRESHOLD`
* Output: `Meal_Allowance_Non_Taxable` (= MEAL_ALLOW_FIXED)
* `Meal_Allowance_Non_Taxable = Min(Total_Meal_Allowance, MEAL_ALLOWANCE_THRESHOLD)`

### PAY-RULE-MEAL-002 — Phần chịu thuế

* Output: `Meal_Allowance_Taxable` (= MEAL_ALLOW_SURP)
* `Meal_Allowance_Taxable = Max(0, Total_Meal_Allowance − Meal_Allowance_Non_Taxable)`

**Ví dụ (seed):** 48h × 15000 = 720000 → toàn bộ non-taxable; 50h × 15000 = 750000 → 730000 non-tax + 20000 taxable → PIT.

### PAY-RULE-HOURLY-001 — Quy đổi lương giờ từ HĐ (dùng chung OT WE/HOL, TIME_DEDUCT, …)

* Input: `salary_type`, `Contract_Salary` (`basic_salary`), `TotalWorkDays` (từ summary = ngày phải làm), `HOURS_PER_WORK_DAY`
* Output: `Hourly_Salary`
* Formula:
  * **IF** `salary_type == HOURLY`: `Hourly_Salary = Contract_Salary`
  * **IF** `salary_type == MONTHLY`:  
    `Hourly_Salary = Contract_Salary / TotalWorkDays / HOURS_PER_WORK_DAY`  
    *Ví dụ OFFICE trên Excel: `basic_salary / 22 / 8` — **22 lấy từ cột TotalWorkDays**, không hardcode trong code.*
* Khi thử việc (đề xuất): dùng `Final_Base_Salary` thay `Contract_Salary` trong nhánh MONTHLY; HOURLY: `Contract_Salary × PROBATION_RATE`.

### PAY-RULE-OT-WE-HOL-001 — OT cuối tuần & lễ (OFFICE + FACTORY)

* Input: `Hourly_Salary` (từ PAY-RULE-HOURLY-001), `OT_Weekend_Hours`, `OT_Holiday_Hours`, `OT_RATE_WEEKEND`, `OT_RATE_HOLIDAY`
* OFFICE **được** nhận OT WE/HOL nếu CSV có giờ (không bị chặn như weekday meal)
* Output:
  * `OT_Salary_Weekend = OT_Weekend_Hours × Hourly_Salary × OT_RATE_WEEKEND`
  * `OT_Salary_Holiday = OT_Holiday_Hours × Hourly_Salary × OT_RATE_HOLIDAY`
  * `Total_OT_Salary = OT_Salary_Weekend + OT_Salary_Holiday`  
    (**không** cộng OT weekday — weekday → meal)

### PAY-RULE-TIME-DEDUCT-001 — Trừ block muộn/sớm

* Input: `Hourly_Salary` (PAY-RULE-HOURLY-001), `Late_Early_Blocks`, `DEDUCTION_RATE_PER_BLOCK`
* Output: `Time_Deduction_Amount`
* `Time_Deduction_Amount = Hourly_Salary × (Late_Early_Blocks × DEDUCTION_RATE_PER_BLOCK)`
* **MUST NOT** hardcode 26

### PAY-RULE-INS-001 — BHXH NLĐ

* Input: `Final_Base_Salary`, `Seniority_Allowance`, `SOCIAL_INS_RATE`  
  (`Responsibility_Allowance` = 0, `Position_Allowance` = 0 CoreMVP)
* `Insurance_Base = Final_Base_Salary + Seniority_Allowance` (+ RESP/POS khi mở lại)
* `Total_Insurance_Deduction = Insurance_Base × SOCIAL_INS_RATE`

### PAY-RULE-PIT-001 — TNCN lũy tiến

* Input: `Taxable_Gross_Income`, `Total_Insurance_Deduction`, `Number_of_Dependents`,  
  `PIT_PERSONAL_EXEMPTION`, `PIT_DEPENDENT_EXEMPTION`, brackets
* `Dependent_Exemption = Number_of_Dependents × PIT_DEPENDENT_EXEMPTION`
* `Net_Taxable_Income = Max(0, Taxable_Gross_Income − Total_Insurance_Deduction − PIT_PERSONAL_EXEMPTION − Dependent_Exemption)`
* `PIT_Tax_Amount = Calculate_Progressive_Tax_VN(Net_Taxable_Income)` theo bracket params

---

## 3. Aggregation

### PAY-AGG-EARNINGS

`Earnings_Total = Final_Base_Salary + Total_OT_Salary + Seniority_Allowance + Total_Meal_Allowance`  
(+ RESP/POS/Bonus/Severance khi có)

### PAY-AGG-TAXABLE-GROSS

`Taxable_Gross_Income = Final_Base_Salary + Total_OT_Salary + Seniority_Allowance + Meal_Allowance_Taxable − Time_Deduction_Amount`  
*(Loại `Meal_Allowance_Non_Taxable` khỏi chịu thuế)*

### PAY-AGG-INTERNAL

`Internal_Deductions_Total = Time_Deduction_Amount`

### PAY-AGG-STATUTORY

`Statutory_Deductions_Total = Total_Insurance_Deduction + PIT_Tax_Amount`

### PAY-AGG-NET

`Net_Pay = Earnings_Total − (Internal_Deductions_Total + Statutory_Deductions_Total)`

---

## 4. Vận hành

1. **Snapshot** toàn bộ param (+ PIT brackets) theo batch khi bấm tính (dù CoreMVP xóa payroll khi import kỳ mới — snapshot phục vụ xem lại nháp hiện tại).
2. **Validate** trước run: `PROBATION_RATE`, `SOCIAL_INS_RATE`, `MEAL_ALLOWANCE_THRESHOLD`, `OT_SALARY_WF`, `WORKING_DAYS_IN_MONTH_*`, `HOURS_PER_WORK_DAY`, đủ PIT brackets.
3. Chỉ tính NV có `current_contract_id`; thiếu → skip + report.
4. Tên biến output MUST thống nhất payslip detail / test.
5. Override Attendance_Bonus / finalize: **treo** CoreMVP (chỉ DRAFT).

---

## 5. Thứ tự gợi ý khi run (1 NV)

1. Đọc summary: `TotalWorkDays`, `ActualWorkDays`, OT…  
2. BASE → PROB → **HOURLY-001** (MONTHLY ÷ `TotalWorkDays` ÷ 8) → SENIOR  
3. MEAL-000 → MEAL-001 → MEAL-002  
4. OT-WE-HOL  
5. TIME_DEDUCT → INS → (AGG taxable) → PIT → NET
