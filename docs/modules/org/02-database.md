# 02-database.md - Org

**Module:** Org  
**Version:** 2.0.0 | **Date:** 2026-07-23 | **Status:** Draft  
**Schema:** `sql/0.hrmdb.sql` + migration `sql/1.org_lifecycle_mvp.sql`

---

## 1. Changes vs Core schema

| Thay đổi | Mô tả |
| :--- | :--- |
| `employee.current_contract_id` | INT NULL, FK → `contract(contract_id)` — HĐ hiện hành |
| Không dùng `employee.status` cho vòng đời | Insert mặc định ACTIVE; list lọc theo current_contract_id |
| `contract.contract_type` | MVP: **1 = PROBATION**, **2 = OFFICIAL** (không dùng FIXED/PERMANENT) |
| Bảng `dependent` | Mới — NPT |
| Permission | Thêm `contract:DELETE`; seed dependent:*; bỏ gán `employee:DELETE`; gỡ hướng `employee-self:READ` khỏi ma trận mới |

## 2. `dependent`

| Column | Type | Notes |
| :--- | :--- | :--- |
| dependent_id | INT PK AI | |
| employee_id | INT NOT NULL FK employee | |
| full_name | VARCHAR(100) NOT NULL | |
| relationship | VARCHAR(50) NOT NULL | Quan hệ |
| tax_code | VARCHAR(20) NULL | MST |
| start_date | DATE NULL | |
| end_date | DATE NULL | |
| status | TINYINT NOT NULL DEFAULT 1 | ActiveStatus |
| created_at | DATETIME DEFAULT CURRENT_TIMESTAMP | |

## 3. Permission seeds (bổ sung)

```text
contract:DELETE
dependent:READ, dependent:CREATE, dependent:UPDATE, dependent:DELETE
```

Gán: HR-MGR/HR-STF đủ employee+contract+dependent; FAC-SUP employee+contract READ; FAC-WRK contract READ + dependent self.

## 4. Home

- Nhân viên → `/employee/list` nếu `employee:READ`
- Hồ sơ cá nhân → `/employee/me` nếu USER có employeeId (dùng chung)
