-- =============================================================================
-- 1.org_lifecycle_mvp.sql — Org wave 2 (HĐ hiện hành, NPT, permissions)
-- Chạy trên DB hrmdb sau 0.hrmdb.sql + 0.seed_data.sql
-- =============================================================================

-- HĐ hiện hành trên emp
ALTER TABLE employee
    ADD COLUMN current_contract_id INT NULL AFTER joining_date;

-- NPT
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

-- FK current_contract (sau khi bảng contract đã có)
ALTER TABLE employee
    ADD CONSTRAINT fk_emp_current_contract
        FOREIGN KEY (current_contract_id) REFERENCES contract(contract_id)
        ON DELETE SET NULL;

-- contract_type MVP: 1 PROBATION, 2 OFFICIAL (comment nghiệp vụ; không đổi TINYINT)
-- Không remap dữ liệu cũ nếu chưa có dòng contract.

-- Permissions mới
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

-- Gỡ employee:DELETE khỏi ma trận (nếu đã gán)
DELETE pp FROM position_permission pp
INNER JOIN permission p ON p.permission_id = pp.permission_id
WHERE p.permission_name = 'employee:DELETE';

-- HR-MGR: contract DELETE + dependent *
INSERT INTO position_permission (position_id, permission_id, granted_by)
SELECT jp.position_id, p.permission_id, (SELECT user_id FROM sys_user WHERE username = 'admin' LIMIT 1)
FROM job_position jp
CROSS JOIN permission p
WHERE jp.position_code = 'HR-MGR'
  AND p.permission_name IN (
    'contract:DELETE',
    'dependent:READ','dependent:CREATE','dependent:UPDATE','dependent:DELETE'
  )
  AND NOT EXISTS (
    SELECT 1 FROM position_permission x
    WHERE x.position_id = jp.position_id AND x.permission_id = p.permission_id
  );

-- HR-STF: contract UPDATE+DELETE + dependent *
INSERT INTO position_permission (position_id, permission_id, granted_by)
SELECT jp.position_id, p.permission_id, (SELECT user_id FROM sys_user WHERE username = 'admin' LIMIT 1)
FROM job_position jp
CROSS JOIN permission p
WHERE jp.position_code = 'HR-STF'
  AND p.permission_name IN (
    'contract:UPDATE','contract:DELETE',
    'dependent:READ','dependent:CREATE','dependent:UPDATE','dependent:DELETE'
  )
  AND NOT EXISTS (
    SELECT 1 FROM position_permission x
    WHERE x.position_id = jp.position_id AND x.permission_id = p.permission_id
  );

-- FAC-WRK: contract READ + dependent self ops
INSERT INTO position_permission (position_id, permission_id, granted_by)
SELECT jp.position_id, p.permission_id, (SELECT user_id FROM sys_user WHERE username = 'admin' LIMIT 1)
FROM job_position jp
CROSS JOIN permission p
WHERE jp.position_code = 'FAC-WRK'
  AND p.permission_name IN (
    'contract:READ',
    'dependent:READ','dependent:CREATE','dependent:UPDATE','dependent:DELETE'
  )
  AND NOT EXISTS (
    SELECT 1 FROM position_permission x
    WHERE x.position_id = jp.position_id AND x.permission_id = p.permission_id
  );
