CREATE DATABASE hrmdb
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE hrmdb;

-- =============================================================================
-- HRMDB schema — TINYINT code columns; semantics in Java enums (ADR-0002)
-- Không dùng CHECK liệt kê giá trị. Validate ở Service + enum code.
-- =============================================================================

-- =============================================================================
-- 1. Tổ chức
-- =============================================================================
CREATE TABLE department (
    department_id    INT AUTO_INCREMENT PRIMARY KEY,
    department_code  VARCHAR(20)  NOT NULL,
    department_name  VARCHAR(100) NOT NULL,
    status            TINYINT      NOT NULL DEFAULT 1,  -- 0=INACTIVE, 1=ACTIVE (ActiveStatus)
    created_at        DATETIME     DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_department_code (department_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE job_position (
    position_id      INT AUTO_INCREMENT PRIMARY KEY,
    position_code    VARCHAR(20)  NOT NULL,
    position_name    VARCHAR(100) NOT NULL,
    department_id    INT          NOT NULL,
    data_scope       TINYINT      NOT NULL DEFAULT 1,  -- 1=SELF, 2=DEPARTMENT, 3=ALL (PositionDataScope)
    status            TINYINT      NOT NULL DEFAULT 1,  -- ActiveStatus
    updated_by        INT          NULL,               -- FK sys_user — ADMIN sửa data_scope (audit)
    updated_at        DATETIME     NULL ON UPDATE CURRENT_TIMESTAMP,
    created_at        DATETIME     DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_position_code (position_code),
    CONSTRAINT fk_pos_dept FOREIGN KEY (department_id)
        REFERENCES department(department_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 2. Tài khoản & phân quyền
-- =============================================================================
CREATE TABLE sys_user (
    user_id          INT AUTO_INCREMENT PRIMARY KEY,
    username         VARCHAR(50)  NOT NULL,
    password_hash    VARCHAR(255) NOT NULL,
    employee_id      INT          NULL,
    status            TINYINT      NOT NULL DEFAULT 1,  -- ActiveStatus
    created_at        DATETIME     DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_user_employee (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE role (
    role_id    INT AUTO_INCREMENT PRIMARY KEY,
    role_name  VARCHAR(50) NOT NULL,
    UNIQUE KEY uk_role_name (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_role (
    user_id  INT NOT NULL,
    role_id  INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES sys_user(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- action: TINYINT — nghĩa do enum Java (PermissionAction); số cụ thể không khóa ở schema (ADR-0002)
-- permission_name: vẫn '{resource}:{ACTION}' để đọc seed/UI/log
CREATE TABLE permission (
    permission_id    INT AUTO_INCREMENT PRIMARY KEY,
    permission_name  VARCHAR(100) NOT NULL,
    resource         VARCHAR(50)  NOT NULL,
    action           TINYINT      NOT NULL,
    description      VARCHAR(255) NULL,
    UNIQUE KEY uk_permission_name (permission_name),
    UNIQUE KEY uk_permission_resource_action (resource, action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE position_permission (
    position_id      INT NOT NULL,
    permission_id    INT NOT NULL,
    granted_by       INT NOT NULL,
    granted_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (position_id, permission_id),
    CONSTRAINT fk_pp_pos FOREIGN KEY (position_id) REFERENCES job_position(position_id) ON DELETE CASCADE,
    CONSTRAINT fk_pp_perm FOREIGN KEY (permission_id) REFERENCES permission(permission_id) ON DELETE CASCADE,
    CONSTRAINT fk_pp_granter FOREIGN KEY (granted_by) REFERENCES sys_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 3. Nhân sự & hợp đồng
-- =============================================================================
CREATE TABLE employee (
    employee_id          INT AUTO_INCREMENT PRIMARY KEY,
    employee_code        VARCHAR(20)  NOT NULL,
    full_name            VARCHAR(100) NOT NULL,
    gender               TINYINT      NOT NULL, -- 1 MALE, 2 FEMALE, 3 OTHER
    birth_date           DATE         NULL,
    bank_account         VARCHAR(50)  NULL,
    position_id          INT          NOT NULL,
    employment_group     TINYINT      NOT NULL, -- 1 OFFICE, 2 FACTORY
    joining_date         DATE         NOT NULL,
    current_contract_id  INT          NULL,     -- HĐ hiện hành (FK thêm sau CREATE contract)
    status               TINYINT      NOT NULL DEFAULT 1, -- ActiveStatus (không dùng cho vòng đời LĐ)
    created_at           DATETIME     DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_employee_code (employee_code),
    CONSTRAINT fk_emp_pos FOREIGN KEY (position_id) REFERENCES job_position(position_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE sys_user
    ADD CONSTRAINT fk_user_emp FOREIGN KEY (employee_id)
        REFERENCES employee(employee_id) ON DELETE SET NULL;

ALTER TABLE job_position
    ADD CONSTRAINT fk_pos_updated_by FOREIGN KEY (updated_by)
        REFERENCES sys_user(user_id);

CREATE TABLE contract (
    contract_id    INT AUTO_INCREMENT PRIMARY KEY,
    employee_id    INT            NOT NULL,
    contract_type  TINYINT        NOT NULL, -- MVP: 1 PROBATION, 2 OFFICIAL
    start_date     DATE           NOT NULL,
    end_date       DATE           NULL,
    basic_salary   DECIMAL(15,2)  NOT NULL,
    salary_type    TINYINT        NOT NULL, -- 1 MONTHLY, 2 HOURLY
    status          TINYINT        NOT NULL DEFAULT 1, -- ActiveStatus
    created_at      DATETIME       DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_contract_emp FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE employee
    ADD CONSTRAINT fk_emp_current_contract
        FOREIGN KEY (current_contract_id) REFERENCES contract(contract_id)
        ON DELETE SET NULL;

CREATE TABLE dependent (
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

-- =============================================================================
-- 4. Chấm công
-- =============================================================================
CREATE TABLE attendance_import (
    import_id    INT AUTO_INCREMENT PRIMARY KEY,
    file_name    VARCHAR(255) NOT NULL,
    import_date  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    imported_by  INT          NOT NULL,
    status        TINYINT      NOT NULL DEFAULT 1, -- 1 PROCESSED, 2 FAILED (log import — không soft-delete)
    CONSTRAINT fk_attimp_user FOREIGN KEY (imported_by) REFERENCES sys_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE daily_attendance (
    attendance_id      INT AUTO_INCREMENT PRIMARY KEY,
    employee_id        INT            NOT NULL,
    import_id          INT            NOT NULL,
    attendance_date    DATE           NOT NULL,
    check_in_time      TIME           NULL,
    check_out_time     TIME           NULL,
    work_hours         DECIMAL(5,2)   DEFAULT 0,
    ot_hours           DECIMAL(5,2)   DEFAULT 0,
    late_early_blocks  INT            DEFAULT 0,
    attendance_status  TINYINT        NOT NULL DEFAULT 2, -- 1 PRESENT, 2 ABSENT, 3 LATE, 4 EARLY, 5 LEAVE
    status               TINYINT        NOT NULL DEFAULT 1, -- ActiveStatus soft-delete
    created_at           DATETIME       DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_emp_date (employee_id, attendance_date),
    CONSTRAINT fk_daily_att_emp FOREIGN KEY (employee_id) REFERENCES employee(employee_id),
    CONSTRAINT fk_daily_att_imp FOREIGN KEY (import_id) REFERENCES attendance_import(import_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE attendance_summary (
    summary_id           INT AUTO_INCREMENT PRIMARY KEY,
    employee_id          INT           NOT NULL,
    summary_period       DATE          NOT NULL,
    total_work_days      DECIMAL(5,1)  DEFAULT 0,
    total_ot_hours       DECIMAL(6,2)  DEFAULT 0,
    total_late_count     INT           DEFAULT 0,
    total_early_count    INT           DEFAULT 0,
    total_absent_days    DECIMAL(5,1)  DEFAULT 0,
    total_leave_days     DECIMAL(5,1)  DEFAULT 0,
    summary_status        TINYINT       NOT NULL DEFAULT 1, -- 1 DRAFT, 2 LOCKED
    status                 TINYINT       NOT NULL DEFAULT 1, -- ActiveStatus
    created_at             DATETIME      DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_emp_period (employee_id, summary_period),
    CONSTRAINT fk_att_sum_emp FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE leave_balance (
    balance_id      INT AUTO_INCREMENT PRIMARY KEY,
    employee_id     INT           NOT NULL,
    year            INT           NOT NULL,
    entitled_days   DECIMAL(5,1)  DEFAULT 0,
    taken_days      DECIMAL(5,1)  DEFAULT 0,
    remaining_days  DECIMAL(5,1)  DEFAULT 0,
    status           TINYINT       NOT NULL DEFAULT 1, -- ActiveStatus
    created_at        DATETIME      DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_leave_balance (employee_id, year),
    CONSTRAINT fk_lv_bal_emp FOREIGN KEY (employee_id) REFERENCES employee(employee_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
