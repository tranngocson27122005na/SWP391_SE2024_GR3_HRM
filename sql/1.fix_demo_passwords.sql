-- =============================================================================
-- Fix password_hash cho tài khoản demo (DB đã seed bằng hash sai trước đây)
-- Plain password sau khi chạy: password
-- =============================================================================
USE hrmdb;

UPDATE sys_user
SET password_hash = '$2a$10$aWJO8RnMe0NbfE3QkTghnuuqUAmNH8dcBNnBeMTW9.Dfg6XI1hk/W'
WHERE username IN ('admin', 'hrm', 'hrs', 'facsup', 'worker01');
