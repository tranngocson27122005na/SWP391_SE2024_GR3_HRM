-- =============================================================================
-- 1.seed_first_contracts_for_employees.sql
-- Mục đích: bổ sung HĐ ACTIVE đầu + gán current_contract_id cho emp DEMO/DEV
--           hiện chưa có bất kỳ dòng contract nào.
--
-- Chạy 1 lần trên DB đã có emp (sau 0.hrmdb + 0.seed + 1.demo_employees_*),
-- KHÔNG thay greenfield 0.*. Idempotent: chạy lại an toàn.
--
-- Quy ước demo:
--   • contract_type = 2 (OFFICIAL) — emp seed đã vào làm lâu; không dùng PROBATION
--   • salary_type   = 1 (MONTHLY)
--   • start_date    = employee.joining_date
--   • end_date      = NULL
--   • basic_salary  placeholder theo chức danh (VND):
--       HR-MGR  20_000_000 | HR-STF 12_000_000
--       FAC-SUP 15_000_000 | FAC-WRK  7_500_000 | khác 10_000_000
--
-- KHÔNG đụng emp đã có ≥1 contract (kể cả INACTIVE).
-- Heal pointer chỉ khi current_contract_id NULL và đúng 1 HĐ ACTIVE.
-- =============================================================================
USE hrmdb;

-- Trước
SELECT COUNT(*) AS emp_total FROM employee;
SELECT COUNT(*) AS contract_total FROM contract;
SELECT COUNT(*) AS emp_without_any_contract
FROM employee e
WHERE NOT EXISTS (SELECT 1 FROM contract c WHERE c.employee_id = e.employee_id);
SELECT COUNT(*) AS emp_null_current FROM employee WHERE current_contract_id IS NULL;

-- -----------------------------------------------------------------------------
-- 1) INSERT HĐ ACTIVE đầu — chỉ emp chưa có contract
-- -----------------------------------------------------------------------------
INSERT INTO contract (
    employee_id, contract_type, start_date, end_date,
    basic_salary, salary_type, status
)
SELECT
    e.employee_id,
    2,                          -- OFFICIAL
    e.joining_date,
    NULL,
    CASE jp.position_code
        WHEN 'HR-MGR'  THEN 20000000.00
        WHEN 'HR-STF'  THEN 12000000.00
        WHEN 'FAC-SUP' THEN 15000000.00
        WHEN 'FAC-WRK' THEN  7500000.00
        ELSE 10000000.00
    END,
    1,                          -- MONTHLY
    1                           -- ACTIVE
FROM employee e
JOIN job_position jp ON jp.position_id = e.position_id
WHERE NOT EXISTS (
    SELECT 1 FROM contract c WHERE c.employee_id = e.employee_id
);

-- -----------------------------------------------------------------------------
-- 2) Gán pointer — chỉ emp NULL + đúng 1 HĐ ACTIVE
-- -----------------------------------------------------------------------------
UPDATE employee e
JOIN (
    SELECT employee_id, MIN(contract_id) AS only_active_id
    FROM contract
    WHERE status = 1
    GROUP BY employee_id
    HAVING COUNT(*) = 1
) one ON one.employee_id = e.employee_id
SET e.current_contract_id = one.only_active_id
WHERE e.current_contract_id IS NULL;

-- Sau
SELECT COUNT(*) AS contract_total FROM contract;
SELECT COUNT(*) AS emp_without_any_contract
FROM employee e
WHERE NOT EXISTS (SELECT 1 FROM contract c WHERE c.employee_id = e.employee_id);
SELECT COUNT(*) AS emp_null_current FROM employee WHERE current_contract_id IS NULL;
SELECT COUNT(*) AS emp_with_current FROM employee WHERE current_contract_id IS NOT NULL;
