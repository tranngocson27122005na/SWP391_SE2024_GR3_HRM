-- =============================================================================
-- SEED DATA — ADR-0001 (position-centric), ADR-0002 (TINYINT; số = enum Java khi có)
-- Chạy sau sql/0.hrmdb.sql  |  Database: hrmdb
-- action: TINYINT — giá trị số trong seed khớp enum PermissionAction (code tự chọn, không tuần tự bắt buộc)
-- MVP: không seed leave-request:* (treo đơn từ)
-- =============================================================================
USE hrmdb;

-- -----------------------------------------------------------------------------
-- 1. Phòng ban
-- -----------------------------------------------------------------------------
INSERT INTO department (department_code, department_name) VALUES
('HR',  'Human Resource'),
('FAC', 'Factory');

-- -----------------------------------------------------------------------------
-- 2. Chức vụ (data_scope: 1=SELF, 2=DEPARTMENT, 3=ALL)
-- -----------------------------------------------------------------------------
INSERT INTO job_position (position_code, position_name, department_id, data_scope) VALUES
('HR-MGR',  'HR Manager',            (SELECT department_id FROM department WHERE department_code = 'HR'),  3),
('HR-STF',  'HR Staff',              (SELECT department_id FROM department WHERE department_code = 'HR'),  3),
('FAC-SUP', 'Production Supervisor', (SELECT department_id FROM department WHERE department_code = 'FAC'), 2),
('FAC-WRK', 'Factory Worker',        (SELECT department_id FROM department WHERE department_code = 'FAC'), 1);

-- -----------------------------------------------------------------------------
-- 3. System Role (chỉ ADMIN / USER)
-- -----------------------------------------------------------------------------
INSERT INTO role (role_name) VALUES
('ADMIN'),
('USER');

-- -----------------------------------------------------------------------------
-- 4. Permission catalog — action = TINYINT code (ADR-0002)
-- -----------------------------------------------------------------------------
INSERT INTO permission (permission_name, resource, action, description) VALUES
('employee:READ',      'employee',      2, 'Xem danh sách / chi tiết nhân viên (theo DataScope)'),
('employee:CREATE',    'employee',      1, 'Tạo nhân viên'),
('employee:UPDATE',    'employee',      3, 'Sửa nhân viên'),
('contract:READ',      'contract',      2, 'Xem hợp đồng'),
('contract:CREATE',    'contract',      1, 'Tạo hợp đồng'),
('contract:UPDATE',    'contract',      3, 'Sửa / kích hoạt hợp đồng'),
('contract:DELETE',    'contract',      4, 'Ngừng hợp đồng (INACTIVE)'),
('dependent:READ',     'dependent',     2, 'Xem người phụ thuộc'),
('dependent:CREATE',   'dependent',     1, 'Tạo người phụ thuộc'),
('dependent:UPDATE',   'dependent',     3, 'Sửa người phụ thuộc'),
('dependent:DELETE',   'dependent',     4, 'Ngừng NPT (INACTIVE)'),
('attendance:READ',    'attendance',    2, 'Xem chấm công'),
('attendance:IMPORT',  'attendance',    5, 'Import chấm công'),
('payslip:READ',       'payslip',       2, 'Xem phiếu lương'),
('payslip:EXPORT',     'payslip',       6, 'Xuất phiếu lương');
-- Ghi chú: employee-self:READ / employee:DELETE không seed ma trận (self-profile dùng chung; không DELETE emp).
-- Catalog cũ có thể còn dòng employee-self / employee:DELETE trên DB đã chạy — không gán position_permission.

-- -----------------------------------------------------------------------------
-- 5. Tài khoản ADMIN (SA)
--    password_hash BCrypt (jBCrypt) cho mật khẩu plain: password
--    ĐỔI NGAY mật khẩu này sau lần đăng nhập đầu tiên.
-- -----------------------------------------------------------------------------
INSERT INTO sys_user (username, password_hash, employee_id, status) VALUES
('admin', '$2a$10$aWJO8RnMe0NbfE3QkTghnuuqUAmNH8dcBNnBeMTW9.Dfg6XI1hk/W', NULL, 1);

INSERT INTO user_role (user_id, role_id) VALUES
((SELECT user_id FROM sys_user WHERE username = 'admin'),
 (SELECT role_id FROM role WHERE role_name = 'ADMIN'));

-- -----------------------------------------------------------------------------
-- 6. position_permission — ma trận tối thiểu MVP (discovery auth §8.2)
--    Cùng employee:READ; phạm vi dữ liệu = data_scope trên job_position.
-- -----------------------------------------------------------------------------
INSERT INTO position_permission (position_id, permission_id, granted_by)
SELECT (SELECT position_id FROM job_position WHERE position_code = 'HR-MGR'),
       p.permission_id,
       (SELECT user_id FROM sys_user WHERE username = 'admin')
FROM permission p
WHERE p.permission_name IN (
  'employee:READ','employee:CREATE','employee:UPDATE',
  'contract:READ','contract:CREATE','contract:UPDATE','contract:DELETE',
  'dependent:READ','dependent:CREATE','dependent:UPDATE','dependent:DELETE',
  'attendance:READ','attendance:IMPORT',
  'payslip:READ','payslip:EXPORT'
);

INSERT INTO position_permission (position_id, permission_id, granted_by)
SELECT (SELECT position_id FROM job_position WHERE position_code = 'HR-STF'),
       p.permission_id,
       (SELECT user_id FROM sys_user WHERE username = 'admin')
FROM permission p
WHERE p.permission_name IN (
  'employee:READ','employee:CREATE','employee:UPDATE',
  'contract:READ','contract:CREATE','contract:UPDATE','contract:DELETE',
  'dependent:READ','dependent:CREATE','dependent:UPDATE','dependent:DELETE',
  'attendance:READ','attendance:IMPORT',
  'payslip:READ'
);

INSERT INTO position_permission (position_id, permission_id, granted_by)
SELECT (SELECT position_id FROM job_position WHERE position_code = 'FAC-SUP'),
       p.permission_id,
       (SELECT user_id FROM sys_user WHERE username = 'admin')
FROM permission p
WHERE p.permission_name IN (
  'employee:READ',
  'contract:READ',
  'attendance:READ'
);

INSERT INTO position_permission (position_id, permission_id, granted_by)
SELECT (SELECT position_id FROM job_position WHERE position_code = 'FAC-WRK'),
       p.permission_id,
       (SELECT user_id FROM sys_user WHERE username = 'admin')
FROM permission p
WHERE p.permission_name IN (
  'contract:READ',
  'dependent:READ','dependent:CREATE','dependent:UPDATE','dependent:DELETE',
  'payslip:READ'
);
