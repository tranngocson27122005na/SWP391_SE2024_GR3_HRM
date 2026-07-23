-- =============================================================================
-- 1.heal_current_contract_pointer.sql
-- Self-heal: gán employee.current_contract_id khi emp có ĐÚNG 1 HĐ ACTIVE
--   mà pointer NULL hoặc trỏ sai.
--
-- KHÔNG tạo contract giả. KHÔNG đụng emp chưa có HĐ ACTIVE (NULL giữ nguyên).
-- Chạy khi diagnose heal_candidates_1_active_null_ptr > 0
--   (xem sql/1.org_lifecycle_final.sql §0).
--
-- Emp chưa có contract nào: heal này vô dụng — dùng
--   sql/1.seed_first_contracts_for_employees.sql (demo/dev)
-- =============================================================================
USE hrmdb;

-- Trước heal
SELECT COUNT(*) AS emp_total FROM employee;
SELECT COUNT(*) AS ctr_active FROM contract WHERE status = 1;
SELECT COUNT(*) AS emp_null_current FROM employee WHERE current_contract_id IS NULL;
SELECT COUNT(*) AS heal_candidates_1_active_null_ptr
FROM employee e
WHERE e.current_contract_id IS NULL
  AND (
    SELECT COUNT(*) FROM contract c
    WHERE c.employee_id = e.employee_id AND c.status = 1
  ) = 1;

-- Heal an toàn: HAVING COUNT(*) = 1 → không đoán khi >1 ACTIVE
UPDATE employee e
JOIN (
    SELECT employee_id, MIN(contract_id) AS only_active_id
    FROM contract
    WHERE status = 1
    GROUP BY employee_id
    HAVING COUNT(*) = 1
) one ON one.employee_id = e.employee_id
SET e.current_contract_id = one.only_active_id
WHERE e.current_contract_id IS NULL
   OR e.current_contract_id <> one.only_active_id;

-- Sau heal
SELECT COUNT(*) AS emp_null_current FROM employee WHERE current_contract_id IS NULL;
SELECT COUNT(*) AS emp_with_current FROM employee WHERE current_contract_id IS NOT NULL;
SELECT COUNT(*) AS heal_candidates_remaining
FROM employee e
WHERE e.current_contract_id IS NULL
  AND (
    SELECT COUNT(*) FROM contract c
    WHERE c.employee_id = e.employee_id AND c.status = 1
  ) = 1;
