-- =============================================================================
-- 1.org_lifecycle_final.sql — Sync/verify org wave 2 (idempotent)
-- Chạy trên DB hrmdb đã có schema gốc. Không sửa 0.hrmdb / 0.seed_data.
--
-- Nghiệp vụ: employee KHI TẠO CÓ THỂ CHƯA CÓ contract.
--   → current_contract_id NULLABLE là đúng; emp không HĐ = hợp lệ.
--   → Script này KHÔNG tạo contract giả / KHÔNG backfill bắt buộc.
-- =============================================================================
USE hrmdb;

-- -----------------------------------------------------------------------------
-- 0) Diagnose emp↔contract (CHẠY LUÔN — xem Result Grid)
--    NULL current_contract_id là HỢP LỆ nếu emp chưa có HĐ ACTIVE.
--    Script này KHÔNG tự điền pointer (heal §5 mặc định TẮT).
--    Có ACTIVE mà pointer lệch → chạy riêng sql/1.heal_current_contract_pointer.sql
-- -----------------------------------------------------------------------------
SELECT COUNT(*) AS emp_total FROM employee;
SELECT COUNT(*) AS ctr_total FROM contract;
SELECT COUNT(*) AS ctr_active FROM contract WHERE status = 1;
SELECT COUNT(*) AS emp_null_current FROM employee WHERE current_contract_id IS NULL;
SELECT COUNT(*) AS emp_with_current FROM employee WHERE current_contract_id IS NOT NULL;

SELECT COUNT(*) AS emp_no_contract_row
FROM employee e
WHERE NOT EXISTS (SELECT 1 FROM contract c WHERE c.employee_id = e.employee_id);

-- Ứng viên heal: đúng 1 HĐ ACTIVE mà pointer NULL
SELECT COUNT(*) AS heal_candidates_1_active_null_ptr
FROM employee e
WHERE e.current_contract_id IS NULL
  AND (
    SELECT COUNT(*) FROM contract c
    WHERE c.employee_id = e.employee_id AND c.status = 1
  ) = 1;

-- Chi tiết bất thường (có ACTIVE, pointer NULL)
SELECT e.employee_id, e.employee_code, e.current_contract_id
FROM employee e
WHERE e.current_contract_id IS NULL
  AND EXISTS (SELECT 1 FROM contract c WHERE c.employee_id = e.employee_id AND c.status = 1);

-- Pointer trỏ HĐ không ACTIVE
SELECT e.employee_id, e.employee_code, e.current_contract_id, c.status
FROM employee e
JOIN contract c ON c.contract_id = e.current_contract_id
WHERE c.status <> 1;

-- >1 HĐ ACTIVE cùng emp (vi phạm BR)
SELECT employee_id, COUNT(*) AS active_cnt
FROM contract WHERE status = 1
GROUP BY employee_id HAVING COUNT(*) > 1;

-- -----------------------------------------------------------------------------
-- 1) Schema: current_contract_id NULLABLE + FK + bảng dependent
-- -----------------------------------------------------------------------------

-- 1a) Cột current_contract_id (NULL = emp chưa có HĐ hiện hành)
SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'employee'
      AND COLUMN_NAME = 'current_contract_id'
);
SET @sql := IF(
    @col_exists = 0,
    'ALTER TABLE employee ADD COLUMN current_contract_id INT NULL AFTER joining_date',
    'SELECT ''OK: employee.current_contract_id already exists'' AS msg'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Đảm bảo NULLABLE (không ép NOT NULL)
SET @is_nullable := (
    SELECT IS_NULLABLE FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'employee'
      AND COLUMN_NAME = 'current_contract_id'
    LIMIT 1
);
SET @sql := IF(
    @is_nullable = 'NO',
    'ALTER TABLE employee MODIFY COLUMN current_contract_id INT NULL',
    'SELECT ''OK: current_contract_id is NULLABLE'' AS msg'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 1b) Bảng dependent
CREATE TABLE IF NOT EXISTS dependent (
    dependent_id   INT AUTO_INCREMENT PRIMARY KEY,
    employee_id    INT          NOT NULL,
    full_name      VARCHAR(100) NOT NULL,
    relationship   VARCHAR(50)  NOT NULL,
    tax_code       VARCHAR(20)  NULL,
    start_date     DATE         NULL,
    end_date       DATE         NULL,
    status         TINYINT      NOT NULL DEFAULT 1,
    created_at     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dep_emp FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 1c) FK fk_emp_current_contract (ON DELETE SET NULL)
SET @fk_exists := (
    SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'employee'
      AND CONSTRAINT_NAME = 'fk_emp_current_contract'
      AND CONSTRAINT_TYPE = 'FOREIGN KEY'
);
SET @sql := IF(
    @fk_exists = 0,
    'ALTER TABLE employee ADD CONSTRAINT fk_emp_current_contract FOREIGN KEY (current_contract_id) REFERENCES contract(contract_id) ON DELETE SET NULL',
    'SELECT ''OK: fk_emp_current_contract already exists'' AS msg'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- -----------------------------------------------------------------------------
-- 2) Permission catalog (wave 2) — NOT EXISTS
-- -----------------------------------------------------------------------------
INSERT INTO permission (permission_name, resource, action, description)
SELECT 'contract:DELETE', 'contract', 4, 'Ngừng hợp đồng (INACTIVE)'
WHERE NOT EXISTS (SELECT 1 FROM permission WHERE permission_name = 'contract:DELETE');

INSERT INTO permission (permission_name, resource, action, description)
SELECT 'dependent:READ', 'dependent', 2, 'Xem người phụ thuộc'
WHERE NOT EXISTS (SELECT 1 FROM permission WHERE permission_name = 'dependent:READ');

INSERT INTO permission (permission_name, resource, action, description)
SELECT 'dependent:CREATE', 'dependent', 1, 'Tạo người phụ thuộc'
WHERE NOT EXISTS (SELECT 1 FROM permission WHERE permission_name = 'dependent:CREATE');

INSERT INTO permission (permission_name, resource, action, description)
SELECT 'dependent:UPDATE', 'dependent', 3, 'Sửa người phụ thuộc'
WHERE NOT EXISTS (SELECT 1 FROM permission WHERE permission_name = 'dependent:UPDATE');

INSERT INTO permission (permission_name, resource, action, description)
SELECT 'dependent:DELETE', 'dependent', 4, 'Ngừng NPT (INACTIVE)'
WHERE NOT EXISTS (SELECT 1 FROM permission WHERE permission_name = 'dependent:DELETE');

-- -----------------------------------------------------------------------------
-- 3) Gỡ khỏi ma trận (không thuộc seed MVP)
-- -----------------------------------------------------------------------------
DELETE pp FROM position_permission pp
INNER JOIN permission p ON p.permission_id = pp.permission_id
WHERE p.permission_name IN ('employee:DELETE', 'employee-self:READ');

-- -----------------------------------------------------------------------------
-- 4) position_permission — ma trận target (0.seed_data + fix FAC-SUP)
--     Idempotent: NOT EXISTS. Không đụng grant attendance/payslip đã có.
-- -----------------------------------------------------------------------------

-- HR-MGR
INSERT INTO position_permission (position_id, permission_id, granted_by)
SELECT jp.position_id, p.permission_id, (SELECT user_id FROM sys_user WHERE username = 'admin' LIMIT 1)
FROM job_position jp
CROSS JOIN permission p
WHERE jp.position_code = 'HR-MGR'
  AND p.permission_name IN (
    'employee:READ','employee:CREATE','employee:UPDATE',
    'contract:READ','contract:CREATE','contract:UPDATE','contract:DELETE',
    'dependent:READ','dependent:CREATE','dependent:UPDATE','dependent:DELETE',
    'attendance:READ','attendance:IMPORT',
    'payslip:READ','payslip:EXPORT'
  )
  AND NOT EXISTS (
    SELECT 1 FROM position_permission x
    WHERE x.position_id = jp.position_id AND x.permission_id = p.permission_id
  );

-- HR-STF
INSERT INTO position_permission (position_id, permission_id, granted_by)
SELECT jp.position_id, p.permission_id, (SELECT user_id FROM sys_user WHERE username = 'admin' LIMIT 1)
FROM job_position jp
CROSS JOIN permission p
WHERE jp.position_code = 'HR-STF'
  AND p.permission_name IN (
    'employee:READ','employee:CREATE','employee:UPDATE',
    'contract:READ','contract:CREATE','contract:UPDATE','contract:DELETE',
    'dependent:READ','dependent:CREATE','dependent:UPDATE','dependent:DELETE',
    'attendance:READ','attendance:IMPORT',
    'payslip:READ'
  )
  AND NOT EXISTS (
    SELECT 1 FROM position_permission x
    WHERE x.position_id = jp.position_id AND x.permission_id = p.permission_id
  );

-- FAC-SUP
INSERT INTO position_permission (position_id, permission_id, granted_by)
SELECT jp.position_id, p.permission_id, (SELECT user_id FROM sys_user WHERE username = 'admin' LIMIT 1)
FROM job_position jp
CROSS JOIN permission p
WHERE jp.position_code = 'FAC-SUP'
  AND p.permission_name IN (
    'employee:READ',
    'contract:READ',
    'attendance:READ'
  )
  AND NOT EXISTS (
    SELECT 1 FROM position_permission x
    WHERE x.position_id = jp.position_id AND x.permission_id = p.permission_id
  );

-- FAC-WRK
INSERT INTO position_permission (position_id, permission_id, granted_by)
SELECT jp.position_id, p.permission_id, (SELECT user_id FROM sys_user WHERE username = 'admin' LIMIT 1)
FROM job_position jp
CROSS JOIN permission p
WHERE jp.position_code = 'FAC-WRK'
  AND p.permission_name IN (
    'contract:READ',
    'dependent:READ','dependent:CREATE','dependent:UPDATE','dependent:DELETE',
    'payslip:READ'
  )
  AND NOT EXISTS (
    SELECT 1 FROM position_permission x
    WHERE x.position_id = jp.position_id AND x.permission_id = p.permission_id
  );

-- -----------------------------------------------------------------------------
-- 5) OPTIONAL self-heal pointer — MẶC ĐỊNH TẮT (giữ comment)
--
-- Ai cập nhật current_contract_id?
--   → ỨNG DỤNG (Java), không phải script này:
--     • EmployeeCommandService.createWithFirstContract — insert emp (null)
--       → insert HĐ ACTIVE → updateCurrentContractId
--     • ContractService.createOrSwitch / activate — set pointer = HĐ mới
--     • ContractService.softDelete — set null nếu đang trỏ HĐ vừa ngừng
--
-- Block §5 chỉ REPAIR dữ liệu cũ lệch (có đúng 1 HĐ ACTIVE mà pointer null/sai).
-- KHÔNG tạo contract. KHÔNG backfill bắt buộc.
-- Emp chưa có dòng contract → NULL là đúng; UPDATE heal = 0 row affected.
--
-- Khi heal_candidates_1_active_null_ptr > 0 (xem §0):
--   → chạy riêng sql/1.heal_current_contract_pointer.sql
--   (hoặc bỏ comment UPDATE bên dưới — cùng logic).
-- -----------------------------------------------------------------------------

-- Re-check ứng viên heal (sau schema/permission)
SELECT COUNT(*) AS heal_candidates_1_active_null_ptr
FROM employee e
WHERE e.current_contract_id IS NULL
  AND (
    SELECT COUNT(*) FROM contract c
    WHERE c.employee_id = e.employee_id AND c.status = 1
  ) = 1;

/*
-- Self-heal: chỉ sửa emp có ĐÚNG 1 HĐ ACTIVE mà pointer null hoặc sai.
-- Prefer: sql/1.heal_current_contract_pointer.sql (UPDATE không bọc comment).
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
*/

-- -----------------------------------------------------------------------------
-- 6) Verify sau sync
-- -----------------------------------------------------------------------------
SELECT
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'employee'
       AND COLUMN_NAME = 'current_contract_id' AND IS_NULLABLE = 'YES') AS has_nullable_current_contract,
    (SELECT COUNT(*) FROM information_schema.TABLES
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'dependent') AS has_dependent_table,
    (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'employee'
       AND CONSTRAINT_NAME = 'fk_emp_current_contract') AS has_fk_current_contract;

SELECT permission_name
FROM permission
WHERE permission_name IN (
  'contract:DELETE',
  'dependent:READ','dependent:CREATE','dependent:UPDATE','dependent:DELETE',
  'employee:DELETE','employee-self:READ'
)
ORDER BY permission_name;

SELECT jp.position_code, p.permission_name
FROM position_permission pp
JOIN job_position jp ON jp.position_id = pp.position_id
JOIN permission p ON p.permission_id = pp.permission_id
WHERE jp.position_code IN ('HR-MGR','HR-STF','FAC-SUP','FAC-WRK')
  AND p.permission_name IN (
    'contract:READ','contract:CREATE','contract:UPDATE','contract:DELETE',
    'dependent:READ','dependent:CREATE','dependent:UPDATE','dependent:DELETE',
    'employee:DELETE','employee-self:READ'
  )
ORDER BY jp.position_code, p.permission_name;

SELECT
    (SELECT COUNT(*) FROM employee) AS emp_total,
    (SELECT COUNT(*) FROM employee WHERE current_contract_id IS NULL) AS emp_null_current,
    (SELECT COUNT(*) FROM employee WHERE current_contract_id IS NOT NULL) AS emp_with_current,
    (SELECT COUNT(*) FROM employee e
     WHERE NOT EXISTS (SELECT 1 FROM contract c WHERE c.employee_id = e.employee_id)
    ) AS emp_no_contract_row;
-- emp_null_current / emp_no_contract_row > 0 là HỢP LỆ (chưa tạo HĐ).
