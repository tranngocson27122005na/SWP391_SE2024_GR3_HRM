-- =============================================================================
-- Tách permission SELF vs LIST + gán cho chức vụ (chạy trên DB đang dùng)
--   employee:READ      = xem danh sách/chi tiết theo DataScope
--   employee-self:READ = xem hồ sơ bản thân (/employee/detail?id={session.employeeId})
-- Sau khi chạy: restart app để PositionPermissionMatrix.reload (hoặc restart Tomcat)
-- =============================================================================
USE hrmdb;

-- 1) Catalog
INSERT INTO permission (permission_name, resource, action, description)
SELECT 'employee-self:READ', 'employee-self', 2, 'Xem hồ sơ nhân viên của chính mình'
WHERE NOT EXISTS (
    SELECT 1 FROM permission WHERE permission_name = 'employee-self:READ'
);

UPDATE permission
SET description = 'Xem danh sách / chi tiết nhân viên (theo DataScope)'
WHERE permission_name = 'employee:READ';

-- 2) Worker: bỏ employee:READ (list), chỉ giữ self + payslip
DELETE pp
FROM position_permission pp
JOIN job_position jp ON jp.position_id = pp.position_id
JOIN permission p ON p.permission_id = pp.permission_id
WHERE jp.position_code = 'FAC-WRK'
  AND p.permission_name = 'employee:READ';

-- 3) Gán employee-self:READ cho mọi chức vụ USER mẫu
INSERT INTO position_permission (position_id, permission_id, granted_by)
SELECT jp.position_id, p.permission_id, (SELECT user_id FROM sys_user WHERE username = 'admin')
FROM job_position jp
CROSS JOIN permission p
WHERE p.permission_name = 'employee-self:READ'
  AND jp.position_code IN ('HR-MGR', 'HR-STF', 'FAC-SUP', 'FAC-WRK')
  AND NOT EXISTS (
      SELECT 1 FROM position_permission x
      WHERE x.position_id = jp.position_id AND x.permission_id = p.permission_id
  );

-- Kiểm tra nhanh
SELECT jp.position_code, p.permission_name
FROM position_permission pp
JOIN job_position jp ON jp.position_id = pp.position_id
JOIN permission p ON p.permission_id = pp.permission_id
WHERE jp.position_code IN ('HR-MGR', 'HR-STF', 'FAC-SUP', 'FAC-WRK')
  AND p.permission_name IN ('employee:READ', 'employee-self:READ', 'payslip:READ')
ORDER BY jp.position_code, p.permission_name;
