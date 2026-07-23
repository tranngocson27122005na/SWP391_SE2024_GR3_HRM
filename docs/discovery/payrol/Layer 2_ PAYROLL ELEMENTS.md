## METADATA

* Artifact_ID: HRMS-L2-PAY-ELEMENT-001
* Version: **2.0.0**
* Document_Status: **Draft for Review**
* Document_Title: PAYROLL ELEMENTS (tham số hóa)
* Dependency_List: Layer 1 v2; Layer 3 v2
* Change_Log: v2.0.0 2026-07-23 — Bỏ hardcode; OT_SALARY_WF = đơn giá giờ weekday → meal; RESP/POS out MVP; rates = param

### Nguyên tắc

* Cột `rate` / `value` / ngưỡng trong bảng dưới = **giá trị seed mặc định**, lưu DB, **HRS sửa được** (permission `payslip:UPDATE`).
* Element catalog seed theo `element_id`; số tiền phát sinh do Layer 3.

---

## 2.1 Earning Elements (CoreMVP)

| element_id | element_name | applicable_group | is_taxable | is_insurable | calc_order | Ghi chú CoreMVP |
| --- | --- | --- | --- | --- | --- | --- |
| **BASE_SALARY** | Lương cơ bản (sau nhánh group) | ALL | YES | YES | 1 | Từ HĐ + rule BASE |
| **PROBATION_SALARY** | Lương sau thử việc | ALL | YES | YES | 1 | Có thể gộp hiển thị với BASE; × `PROBATION_RATE` |
| **SENIORITY_ALLOW** | Phụ cấp thâm niên | ALL | YES | YES | 2 | Rule SENIOR |
| **MEAL_ALLOW_FIXED** | Ăn ca phần ≤ trần | ALL | **NO** | NO | 2 | Từ meal gross |
| **MEAL_ALLOW_SURP** | Ăn ca phần > trần | ALL | **YES** | NO | 2 | Vào taxable |
| **OT_SALARY_WE** | Lương OT cuối tuần | ALL | YES | NO | 3 | Giờ WE × hourly × `OT_RATE_WEEKEND` |
| **OT_SALARY_HOL** | Lương OT lễ/Tết | ALL | YES | NO | 3 | Giờ HOL × hourly × `OT_RATE_HOLIDAY` |

### Out of CoreMVP (không seed bắt buộc / amount = 0)

| element_id | Lý do |
| --- | --- |
| **RESP_ALLOW** | BA chưa chốt |
| **POS_ALLOW** | BA chưa chốt |
| **SEVERANCE_ALLOWANCE** | Treo |
| **OT_SALARY_WF** như *dòng lương OT weekday riêng* | Weekday **không** trả OT theo hệ số 1.5; weekday tạo **meal** (xem param `OT_SALARY_WF` bên dưới) |

### Ý nghĩa đặc biệt: param / đơn giá `OT_SALARY_WF`

* **`OT_SALARY_WF`**: đơn giá **VND / giờ OT ngày thường** (bản chất lương OT theo giờ weekday), **tham số động**.
* Seed ví dụ: **15000**.
* `Total_Meal_Allowance = OT_Weekday_Hours × OT_SALARY_WF` (chỉ FACTORY; OFFICE weekday hours = 0).
* **MUST NOT** vừa cộng meal từ weekday **vừa** cộng thêm dòng OT weekday theo `Hourly_Salary × 1.5`.

---

## 2.2 Statutory — bảo hiểm (NLĐ)

Tỷ lệ = **param**; seed ví dụ:

| element_id / param | Ý nghĩa | Seed ví dụ |
| --- | --- | --- |
| **BHXH_EMP** | BHXH NLĐ | 0.08 |
| **BHYT_EMP** | BHYT NLĐ | 0.015 |
| **BHTN_EMP** | BHTN NLĐ | 0.01 |
| **SOCIAL_INS_RATE** | Gộp NLĐ (nếu engine gộp) | 0.105 |

Công ty đóng (BHXH_CO…) — **treo** hiển thị phiếu CoreMVP nếu chưa cần.

### Thuế TNCN — bậc = param động

| param | Seed ví dụ upper / rate |
| --- | --- |
| PIT_BRACKET_1 … 6 | 5M/5%, 10M/10%, 18M/15%, 32M/20%, 52M/25%, 80M/30% |
| PIT_BRACKET_7_RATE | 35% (không upper) |
| PIT_PERSONAL_EXEMPTION | 11_000_000 |
| PIT_DEPENDENT_EXEMPTION | 4_400_000 |

---

## 2.3 Internal deduction

| element_id | trigger | Params liên quan | Seed ví dụ |
| --- | --- | --- | --- |
| **TIME_DEDUCT** | Muộn/sớm | `DEDUCTION_BLOCK_MINUTES`=30; `DEDUCTION_RATE_PER_BLOCK`=0.5 | Cap % nếu có = param |

Mẫu số giờ lương: `Final_Base_Salary / Working_Days_In_Month / HOURS_PER_WORK_DAY` — **không** /26 cứng.

---

## 2.4 Leave (tham chiếu)

| leave_type | group | base_days (seed) | accrual |
| --- | --- | --- | --- |
| ANNUAL_LEAVE | OFFICE | 12 | +1 ngày/5 năm (param rule) |
| ANNUAL_LEAVE | FACTORY | 14 | tương tự |
| UNPAID_LEAVE | ALL | 0 | — |

CoreMVP: lấy số ngày từ CSV summary; không bắt buộc leave_balance sync.

---

## 2.5 Tham số meal / OT (tóm tắt seed)

| param_id | Seed ví dụ | Ghi chú |
| --- | --- | --- |
| **OT_SALARY_WF** | 15000 | VND/giờ → meal gross |
| **MEAL_ALLOWANCE_THRESHOLD** | 730000 | Trần miễn thuế ăn ca (VN); sửa UI |
| **OT_RATE_WEEKEND** | 2.0 | Hệ số |
| **OT_RATE_HOLIDAY** | 3.0 | Hệ số |
| **OT_RATE_WEEKDAY** | 1.5 | **Không dùng** trả weekday CoreMVP (giữ param nếu luật/UI; engine weekday → meal) |
| **HOURS_PER_WORK_DAY** | 8 | `Actual_Working_Hours = ActualWorkDays × này` |
| **WORKING_DAYS_IN_MONTH_OFFICE** | 22 | |
| **WORKING_DAYS_IN_MONTH_FACTORY** | 26 | |
| **PROBATION_RATE** | 0.85 | |

Mở rộng (hazard, housing…): treo — không CoreMVP.
