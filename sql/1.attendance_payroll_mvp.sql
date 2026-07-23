-- =============================================================================
-- 1.attendance_payroll_mvp.sql
-- Attendance summary (CoreMVP) + Payroll draft tables + param/element seed
-- Chạy trên hrmdb SAU khi đã có 0.hrmdb + org migrations.
-- =============================================================================
USE hrmdb;

-- -----------------------------------------------------------------------------
-- A) attendance_summary — mở rộng cột CoreMVP
-- -----------------------------------------------------------------------------
ALTER TABLE attendance_summary
    ADD COLUMN import_id INT NULL AFTER employee_id,
    ADD COLUMN period_month TINYINT NULL AFTER import_id,
    ADD COLUMN period_year SMALLINT NULL AFTER period_month,
    ADD COLUMN actual_work_days DECIMAL(5,2) NOT NULL DEFAULT 0 AFTER total_work_days,
    ADD COLUMN paid_leave_days DECIMAL(5,2) NOT NULL DEFAULT 0 AFTER actual_work_days,
    ADD COLUMN unpaid_leave_days DECIMAL(5,2) NOT NULL DEFAULT 0 AFTER paid_leave_days,
    ADD COLUMN holiday_days DECIMAL(5,2) NOT NULL DEFAULT 0 AFTER unpaid_leave_days,
    ADD COLUMN ot_weekday_hours DECIMAL(8,2) NOT NULL DEFAULT 0 AFTER holiday_days,
    ADD COLUMN ot_weekend_hours DECIMAL(8,2) NOT NULL DEFAULT 0 AFTER ot_weekday_hours,
    ADD COLUMN ot_holiday_hours DECIMAL(8,2) NOT NULL DEFAULT 0 AFTER ot_weekend_hours,
    ADD COLUMN late_early_blocks INT NOT NULL DEFAULT 0 AFTER ot_holiday_hours;

-- Backfill period từ summary_period nếu có
-- Dùng summary_id (PK) để tương thích MySQL Workbench safe update mode
UPDATE attendance_summary
SET period_month = MONTH(summary_period),
    period_year = YEAR(summary_period)
WHERE summary_id > 0
  AND summary_period IS NOT NULL
  AND (period_month IS NULL OR period_year IS NULL);

ALTER TABLE attendance_summary
    ADD CONSTRAINT fk_att_sum_import
        FOREIGN KEY (import_id) REFERENCES attendance_import(import_id)
        ON DELETE SET NULL;

-- Unique kỳ mới (cho phép summary_period cũ vẫn còn)
ALTER TABLE attendance_summary
    ADD UNIQUE KEY uk_att_sum_emp_period (employee_id, period_year, period_month);

ALTER TABLE attendance_import
    ADD COLUMN total_record INT NOT NULL DEFAULT 0 AFTER imported_by;

-- -----------------------------------------------------------------------------
-- B) Payroll catalog + runtime
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS payroll_element (
    element_id    INT AUTO_INCREMENT PRIMARY KEY,
    element_code  VARCHAR(50)  NOT NULL,
    element_name  VARCHAR(100) NOT NULL,
    category      TINYINT      NOT NULL, -- 1 EARNING, 2 STATUTORY, 3 INTERNAL_DEDUCTION
    is_taxable    TINYINT      NOT NULL DEFAULT 0,
    is_insurable  TINYINT      NOT NULL DEFAULT 0,
    calc_order    INT          NOT NULL DEFAULT 0,
    status        TINYINT      NOT NULL DEFAULT 1,
    UNIQUE KEY uk_payroll_element_code (element_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS payroll_param (
    param_id     INT AUTO_INCREMENT PRIMARY KEY,
    param_code   VARCHAR(50)  NOT NULL,
    param_value  DECIMAL(18,6) NOT NULL,
    note         VARCHAR(255) NULL,
    status       TINYINT      NOT NULL DEFAULT 1,
    updated_at   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_payroll_param_code (param_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS payroll_batch (
    batch_id       INT AUTO_INCREMENT PRIMARY KEY,
    batch_name     VARCHAR(100) NOT NULL,
    period_month   TINYINT      NOT NULL,
    period_year    SMALLINT     NOT NULL,
    import_id      INT          NULL,
    run_at         DATETIME     NULL,
    run_by         INT          NULL,
    batch_status   TINYINT      NOT NULL DEFAULT 1, -- 1 DRAFT
    total_net      DECIMAL(15,2) NOT NULL DEFAULT 0,
    status         TINYINT      NOT NULL DEFAULT 1,
    created_at     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_batch_import FOREIGN KEY (import_id) REFERENCES attendance_import(import_id) ON DELETE SET NULL,
    CONSTRAINT fk_batch_user FOREIGN KEY (run_by) REFERENCES sys_user(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS payslip (
    payslip_id   INT AUTO_INCREMENT PRIMARY KEY,
    batch_id     INT NOT NULL,
    employee_id  INT NOT NULL,
    contract_id  INT NOT NULL,
    payslip_status TINYINT NOT NULL DEFAULT 1, -- 1 DRAFT
    net_pay      DECIMAL(15,2) NOT NULL DEFAULT 0,
    status       TINYINT NOT NULL DEFAULT 1,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_payslip_batch_emp (batch_id, employee_id),
    CONSTRAINT fk_payslip_batch FOREIGN KEY (batch_id) REFERENCES payroll_batch(batch_id) ON DELETE CASCADE,
    CONSTRAINT fk_payslip_emp FOREIGN KEY (employee_id) REFERENCES employee(employee_id),
    CONSTRAINT fk_payslip_contract FOREIGN KEY (contract_id) REFERENCES contract(contract_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS payslip_detail (
    detail_id    INT AUTO_INCREMENT PRIMARY KEY,
    payslip_id   INT NOT NULL,
    element_id   INT NOT NULL,
    amount       DECIMAL(15,2) NOT NULL DEFAULT 0,
    remark       VARCHAR(255) NULL,
    UNIQUE KEY uk_payslip_element (payslip_id, element_id),
    CONSTRAINT fk_pdet_payslip FOREIGN KEY (payslip_id) REFERENCES payslip(payslip_id) ON DELETE CASCADE,
    CONSTRAINT fk_pdet_element FOREIGN KEY (element_id) REFERENCES payroll_element(element_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS payroll_batch_param (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    batch_id     INT NOT NULL,
    param_code   VARCHAR(50) NOT NULL,
    param_value  DECIMAL(18,6) NOT NULL,
    CONSTRAINT fk_bparam_batch FOREIGN KEY (batch_id) REFERENCES payroll_batch(batch_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS payroll_batch_pit_bracket (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    batch_id      INT NOT NULL,
    bracket_level INT NOT NULL,
    lower_bound   DECIMAL(15,2) NOT NULL,
    upper_bound   DECIMAL(15,2) NULL,
    rate          DECIMAL(8,6) NOT NULL,
    CONSTRAINT fk_bpit_batch FOREIGN KEY (batch_id) REFERENCES payroll_batch(batch_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- C) Seed elements + params
-- -----------------------------------------------------------------------------
INSERT INTO payroll_element (element_code, element_name, category, is_taxable, is_insurable, calc_order)
SELECT * FROM (
    SELECT 'BASE_SALARY' AS c, 'Lương cơ bản' AS n, 1 AS cat, 1 AS tax, 1 AS ins, 1 AS ord UNION ALL
    SELECT 'SENIORITY_ALLOW', 'Phụ cấp thâm niên', 1, 1, 1, 2 UNION ALL
    SELECT 'MEAL_ALLOW_FIXED', 'Ăn ca miễn thuế', 1, 0, 0, 2 UNION ALL
    SELECT 'MEAL_ALLOW_SURP', 'Ăn ca chịu thuế', 1, 1, 0, 2 UNION ALL
    SELECT 'OT_SALARY_WE', 'OT cuối tuần', 1, 1, 0, 3 UNION ALL
    SELECT 'OT_SALARY_HOL', 'OT lễ/Tết', 1, 1, 0, 3 UNION ALL
    SELECT 'TIME_DEDUCT', 'Khấu trừ muộn/sớm', 3, 0, 0, 4 UNION ALL
    SELECT 'BH_EMP', 'BHXH+BHYT+BHTN NLĐ', 2, 0, 0, 5 UNION ALL
    SELECT 'PIT_TAX', 'Thuế TNCN', 2, 0, 0, 6
) x
WHERE NOT EXISTS (SELECT 1 FROM payroll_element e WHERE e.element_code = x.c);

INSERT INTO payroll_param (param_code, param_value, note)
SELECT * FROM (
    SELECT 'PROBATION_RATE' AS c, 0.850000 AS v, 'Tỷ lệ thử việc' AS n UNION ALL
    SELECT 'DEDUCTION_BLOCK_MINUTES', 30, 'Phút/block' UNION ALL
    SELECT 'DEDUCTION_RATE_PER_BLOCK', 0.5, 'Giờ lương/block' UNION ALL
    SELECT 'HOURS_PER_WORK_DAY', 8, 'Giờ/ngày' UNION ALL
    SELECT 'SOCIAL_INS_RATE', 0.105, 'BH NLĐ gộp' UNION ALL
    SELECT 'MEAL_ALLOWANCE_THRESHOLD', 730000, 'Trần miễn thuế ăn ca' UNION ALL
    SELECT 'OT_SALARY_WF', 15000, 'VND/giờ OT weekday → meal' UNION ALL
    SELECT 'OT_RATE_WEEKEND', 2.0, 'Hệ số OT cuối tuần' UNION ALL
    SELECT 'OT_RATE_HOLIDAY', 3.0, 'Hệ số OT lễ' UNION ALL
    SELECT 'PIT_PERSONAL_EXEMPTION', 11000000, 'Giảm trừ bản thân' UNION ALL
    SELECT 'PIT_DEPENDENT_EXEMPTION', 4400000, 'Giảm trừ / NPT' UNION ALL
    SELECT 'PIT_BRACKET_1_UPPER', 5000000, NULL UNION ALL
    SELECT 'PIT_BRACKET_1_RATE', 0.05, NULL UNION ALL
    SELECT 'PIT_BRACKET_2_UPPER', 10000000, NULL UNION ALL
    SELECT 'PIT_BRACKET_2_RATE', 0.10, NULL UNION ALL
    SELECT 'PIT_BRACKET_3_UPPER', 18000000, NULL UNION ALL
    SELECT 'PIT_BRACKET_3_RATE', 0.15, NULL UNION ALL
    SELECT 'PIT_BRACKET_4_UPPER', 32000000, NULL UNION ALL
    SELECT 'PIT_BRACKET_4_RATE', 0.20, NULL UNION ALL
    SELECT 'PIT_BRACKET_5_UPPER', 52000000, NULL UNION ALL
    SELECT 'PIT_BRACKET_5_RATE', 0.25, NULL UNION ALL
    SELECT 'PIT_BRACKET_6_UPPER', 80000000, NULL UNION ALL
    SELECT 'PIT_BRACKET_6_RATE', 0.30, NULL UNION ALL
    SELECT 'PIT_BRACKET_7_RATE', 0.35, NULL
) p
WHERE NOT EXISTS (SELECT 1 FROM payroll_param x WHERE x.param_code = p.c);

-- payslip:UPDATE (sửa param)
INSERT INTO permission (permission_name, resource, action, description)
SELECT 'payslip:UPDATE', 'payslip', 3, 'Sửa tham số tính lương'
WHERE NOT EXISTS (SELECT 1 FROM permission WHERE permission_name = 'payslip:UPDATE');

INSERT INTO permission (permission_name, resource, action, description)
SELECT 'payslip:CREATE', 'payslip', 1, 'Chạy tính lương (nháp)'
WHERE NOT EXISTS (SELECT 1 FROM permission WHERE permission_name = 'payslip:CREATE');

INSERT INTO position_permission (position_id, permission_id, granted_by)
SELECT jp.position_id, p.permission_id, (SELECT user_id FROM sys_user WHERE username = 'admin' LIMIT 1)
FROM job_position jp
CROSS JOIN permission p
WHERE jp.position_code IN ('HR-MGR', 'HR-STF')
  AND p.permission_name IN ('payslip:UPDATE', 'payslip:CREATE')
  AND NOT EXISTS (
      SELECT 1 FROM position_permission x
      WHERE x.position_id = jp.position_id AND x.permission_id = p.permission_id
  );
