-- =============================================================================
-- 1.demo_employees_seed_data.sql
-- Demo employees + HĐ đầu + USER accounts for local trial
-- (chạy SAU 0.hrmdb.sql + 0.seed_data.sql)
-- Database: hrmdb
--
-- Nội dung:
--   1 × HR Manager            (HR-MGR)  → login: hrm      / password
--   1 × HR Staff              (HR-STF)  → login: hrs      / password
--   1 × Production Supervisor (FAC-SUP) → login: facsup   / password
--  20 × Factory Worker        (FAC-WRK) — hồ sơ; worker01 login để test DataScope SELF
--   + 1 HĐ ACTIVE OFFICIAL / emp (current_contract_id) — tránh seed lệch “có HĐ hiện hành”
--
-- password_hash = BCrypt (jBCrypt) của plain "password" (khớp 0.seed_data.sql)
--
-- DB đã seed emp cũ không HĐ: chạy thêm
--   sql/1.seed_first_contracts_for_employees.sql
-- =============================================================================
USE hrmdb;

-- -----------------------------------------------------------------------------
-- 1. Employees — 3 chức danh chính + 20 công nhân
-- -----------------------------------------------------------------------------
INSERT INTO employee (
    employee_code, full_name, gender, birth_date, bank_account,
    position_id, employment_group, joining_date, status
) VALUES
-- HR Manager
('EMP-HRM-001', 'Nguyen Van Quan', 1, '1988-03-12', '012345678901',
 (SELECT position_id FROM job_position WHERE position_code = 'HR-MGR'), 1, '2018-01-15', 1),
-- HR Staff
('EMP-HRS-001', 'Tran Thi Hoa', 2, '1992-07-21', '012345678902',
 (SELECT position_id FROM job_position WHERE position_code = 'HR-STF'), 1, '2020-05-10', 1),
-- Production Supervisor
('EMP-SUP-001', 'Le Van Hung', 1, '1985-11-05', '012345678903',
 (SELECT position_id FROM job_position WHERE position_code = 'FAC-SUP'), 2, '2017-09-01', 1),
-- 20 Factory Workers
('EMP-WRK-001', 'Pham Van An',       1, '1995-01-20', '100000000001',
 (SELECT position_id FROM job_position WHERE position_code = 'FAC-WRK'), 2, '2021-03-01', 1),
('EMP-WRK-002', 'Nguyen Van Binh',   1, '1994-02-11', '100000000002',
 (SELECT position_id FROM job_position WHERE position_code = 'FAC-WRK'), 2, '2021-04-01', 1),
('EMP-WRK-003', 'Tran Van Cuong',    1, '1993-05-18', '100000000003',
 (SELECT position_id FROM job_position WHERE position_code = 'FAC-WRK'), 2, '2021-04-15', 1),
('EMP-WRK-004', 'Le Thi Dung',       2, '1996-08-09', '100000000004',
 (SELECT position_id FROM job_position WHERE position_code = 'FAC-WRK'), 2, '2021-05-01', 1),
('EMP-WRK-005', 'Pham Van Em',       1, '1991-12-30', '100000000005',
 (SELECT position_id FROM job_position WHERE position_code = 'FAC-WRK'), 2, '2021-05-20', 1),
('EMP-WRK-006', 'Hoang Thi Phuong',  2, '1997-03-14', '100000000006',
 (SELECT position_id FROM job_position WHERE position_code = 'FAC-WRK'), 2, '2021-06-01', 1),
('EMP-WRK-007', 'Vu Van Giang',      1, '1990-09-22', '100000000007',
 (SELECT position_id FROM job_position WHERE position_code = 'FAC-WRK'), 2, '2021-06-15', 1),
('EMP-WRK-008', 'Dang Thi Hanh',     2, '1998-01-07', '100000000008',
 (SELECT position_id FROM job_position WHERE position_code = 'FAC-WRK'), 2, '2021-07-01', 1),
('EMP-WRK-009', 'Bui Van Khoa',      1, '1992-04-25', '100000000009',
 (SELECT position_id FROM job_position WHERE position_code = 'FAC-WRK'), 2, '2021-07-20', 1),
('EMP-WRK-010', 'Do Thi Lan',        2, '1995-10-03', '100000000010',
 (SELECT position_id FROM job_position WHERE position_code = 'FAC-WRK'), 2, '2021-08-01', 1),
('EMP-WRK-011', 'Ngo Van Minh',      1, '1989-06-16', '100000000011',
 (SELECT position_id FROM job_position WHERE position_code = 'FAC-WRK'), 2, '2021-08-15', 1),
('EMP-WRK-012', 'Duong Thi Nga',     2, '1996-11-28', '100000000012',
 (SELECT position_id FROM job_position WHERE position_code = 'FAC-WRK'), 2, '2022-01-10', 1),
('EMP-WRK-013', 'Mai Van Phong',     1, '1993-07-04', '100000000013',
 (SELECT position_id FROM job_position WHERE position_code = 'FAC-WRK'), 2, '2022-02-01', 1),
('EMP-WRK-014', 'Trinh Thi Quynh',   2, '1999-02-19', '100000000014',
 (SELECT position_id FROM job_position WHERE position_code = 'FAC-WRK'), 2, '2022-03-01', 1),
('EMP-WRK-015', 'Ly Van Son',        1, '1991-08-08', '100000000015',
 (SELECT position_id FROM job_position WHERE position_code = 'FAC-WRK'), 2, '2022-04-01', 1),
('EMP-WRK-016', 'Cao Thi Trang',     2, '1994-12-01', '100000000016',
 (SELECT position_id FROM job_position WHERE position_code = 'FAC-WRK'), 2, '2022-05-01', 1),
('EMP-WRK-017', 'Ha Van Uyen',       1, '1990-03-27', '100000000017',
 (SELECT position_id FROM job_position WHERE position_code = 'FAC-WRK'), 2, '2022-06-01', 1),
('EMP-WRK-018', 'Tong Thi Van',      2, '1997-09-13', '100000000018',
 (SELECT position_id FROM job_position WHERE position_code = 'FAC-WRK'), 2, '2022-07-01', 1),
('EMP-WRK-019', 'Chu Van Xuan',      1, '1988-05-06', '100000000019',
 (SELECT position_id FROM job_position WHERE position_code = 'FAC-WRK'), 2, '2022-08-01', 1),
('EMP-WRK-020', 'Luu Thi Yen',       2, '1995-04-17', '100000000020',
 (SELECT position_id FROM job_position WHERE position_code = 'FAC-WRK'), 2, '2023-01-15', 1);

-- -----------------------------------------------------------------------------
-- 1b. Hợp đồng ACTIVE đầu + current_contract_id
--     Idempotent theo emp: chỉ INSERT khi emp chưa có bất kỳ contract nào.
--     OFFICIAL=2, MONTHLY=1; lương placeholder theo chức danh (cùng
--     sql/1.seed_first_contracts_for_employees.sql).
-- -----------------------------------------------------------------------------
INSERT INTO contract (
    employee_id, contract_type, start_date, end_date,
    basic_salary, salary_type, status
)
SELECT
    e.employee_id,
    2,
    e.joining_date,
    NULL,
    CASE jp.position_code
        WHEN 'HR-MGR'  THEN 20000000.00
        WHEN 'HR-STF'  THEN 12000000.00
        WHEN 'FAC-SUP' THEN 15000000.00
        WHEN 'FAC-WRK' THEN  7500000.00
        ELSE 10000000.00
    END,
    1,
    1
FROM employee e
JOIN job_position jp ON jp.position_id = e.position_id
WHERE e.employee_code IN (
    'EMP-HRM-001', 'EMP-HRS-001', 'EMP-SUP-001',
    'EMP-WRK-001', 'EMP-WRK-002', 'EMP-WRK-003', 'EMP-WRK-004', 'EMP-WRK-005',
    'EMP-WRK-006', 'EMP-WRK-007', 'EMP-WRK-008', 'EMP-WRK-009', 'EMP-WRK-010',
    'EMP-WRK-011', 'EMP-WRK-012', 'EMP-WRK-013', 'EMP-WRK-014', 'EMP-WRK-015',
    'EMP-WRK-016', 'EMP-WRK-017', 'EMP-WRK-018', 'EMP-WRK-019', 'EMP-WRK-020'
)
  AND NOT EXISTS (
      SELECT 1 FROM contract c WHERE c.employee_id = e.employee_id
  );

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
  AND e.employee_code IN (
    'EMP-HRM-001', 'EMP-HRS-001', 'EMP-SUP-001',
    'EMP-WRK-001', 'EMP-WRK-002', 'EMP-WRK-003', 'EMP-WRK-004', 'EMP-WRK-005',
    'EMP-WRK-006', 'EMP-WRK-007', 'EMP-WRK-008', 'EMP-WRK-009', 'EMP-WRK-010',
    'EMP-WRK-011', 'EMP-WRK-012', 'EMP-WRK-013', 'EMP-WRK-014', 'EMP-WRK-015',
    'EMP-WRK-016', 'EMP-WRK-017', 'EMP-WRK-018', 'EMP-WRK-019', 'EMP-WRK-020'
  );

-- -----------------------------------------------------------------------------
-- 2. sys_user (realm USER) — gắn employee
-- -----------------------------------------------------------------------------
INSERT INTO sys_user (username, password_hash, employee_id, status) VALUES
('hrm',
 '$2a$10$aWJO8RnMe0NbfE3QkTghnuuqUAmNH8dcBNnBeMTW9.Dfg6XI1hk/W',
 (SELECT employee_id FROM employee WHERE employee_code = 'EMP-HRM-001'), 1),
('hrs',
 '$2a$10$aWJO8RnMe0NbfE3QkTghnuuqUAmNH8dcBNnBeMTW9.Dfg6XI1hk/W',
 (SELECT employee_id FROM employee WHERE employee_code = 'EMP-HRS-001'), 1),
('facsup',
 '$2a$10$aWJO8RnMe0NbfE3QkTghnuuqUAmNH8dcBNnBeMTW9.Dfg6XI1hk/W',
 (SELECT employee_id FROM employee WHERE employee_code = 'EMP-SUP-001'), 1),
('worker01',
 '$2a$10$aWJO8RnMe0NbfE3QkTghnuuqUAmNH8dcBNnBeMTW9.Dfg6XI1hk/W',
 (SELECT employee_id FROM employee WHERE employee_code = 'EMP-WRK-001'), 1);

INSERT INTO user_role (user_id, role_id)
SELECT u.user_id, r.role_id
FROM sys_user u
CROSS JOIN role r
WHERE r.role_name = 'USER'
  AND u.username IN ('hrm', 'hrs', 'facsup', 'worker01');
