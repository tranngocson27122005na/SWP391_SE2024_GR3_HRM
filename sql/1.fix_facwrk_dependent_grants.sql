-- Bổ sung grant FAC-WRK / FAC-SUP sau org lifecycle (DB đã chạy 1.org_lifecycle_mvp.sql)
-- Safe: NOT EXISTS

INSERT INTO position_permission (position_id, permission_id, granted_by)
SELECT jp.position_id, p.permission_id, (SELECT user_id FROM sys_user WHERE username = 'admin' LIMIT 1)
FROM job_position jp
CROSS JOIN permission p
WHERE jp.position_code = 'FAC-SUP'
  AND p.permission_name = 'contract:READ'
  AND NOT EXISTS (
    SELECT 1 FROM position_permission x
    WHERE x.position_id = jp.position_id AND x.permission_id = p.permission_id
  );

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

-- Gỡ employee-self khỏi ma trận nếu còn (self-profile = whitelist)
DELETE pp FROM position_permission pp
INNER JOIN permission p ON p.permission_id = pp.permission_id
WHERE p.permission_name = 'employee-self:READ';
