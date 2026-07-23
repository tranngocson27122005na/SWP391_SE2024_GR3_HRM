# 03-design.md - Org

**Module:** Org  
**Version:** 2.0.0 | **Date:** 2026-07-23 | **Status:** Draft

---

## 1. URL Mapping

JSP: `/WEB-INF/views/org/{employee|contract|dependent}/`  
Controller: `com.hrm.controller.org.{employee|contract|dependent}`

### Employee

| Method | URL | Controller | Permission |
| :--- | :--- | :--- | :--- |
| GET | `/employee/list` | ListEmployeeController | `employee:READ` |
| GET | `/employee/detail` | DetailEmployeeController | `employee:READ` |
| GET | `/employee/me` | MyEmployeeController | Dùng chung (USER + employeeId) |
| GET+POST | `/employee/create` | CreateEmployeeController | CREATE emp (+ CREATE contract trong POST) |
| GET | `/employee/edit` | EditEmployeeController | `employee:UPDATE` |
| POST | `/employee/update` | UpdateEmployeeController | `employee:UPDATE` |

### Contract

| Method | URL | Controller | Permission |
| :--- | :--- | :--- | :--- |
| GET | `/contract/list` | ListContractController | `contract:READ` (query `employeeId`) |
| GET | `/contract/detail` | DetailContractController | `contract:READ` |
| GET+POST | `/contract/create` | CreateContractController | `contract:CREATE` |
| GET | `/contract/edit` | EditContractController | `contract:UPDATE` |
| POST | `/contract/update` | UpdateContractController | `contract:UPDATE` (activate / sửa) |
| POST | `/contract/delete` | DeleteContractController | `contract:DELETE` |

### Dependent

| Method | URL | Controller | Permission |
| :--- | :--- | :--- | :--- |
| GET | `/dependent/list` | ListDependentController | `dependent:READ` (`employeeId`) |
| GET+POST | `/dependent/create` | CreateDependentController | `dependent:CREATE` |
| GET | `/dependent/edit` | EditDependentController | `dependent:UPDATE` |
| POST | `/dependent/update` | UpdateDependentController | `dependent:UPDATE` |
| POST | `/dependent/delete` | DeleteDependentController | `dependent:DELETE` → INACTIVE |

AuthFilter: prefix `/employee/`, `/contract/`, `/dependent/`; `/employee/me` whitelist USER có employeeId.

## 2. DTO

- `EmployeeFormRequest`, `EmployeeResponse` (+ `currentContractId`, labels)
- `ContractFormRequest`, `ContractResponse`
- `DependentFormRequest`, `DependentResponse`

## 3. Enums

- `ContractType`: PROBATION(1), OFFICIAL(2)
- `SalaryType`: MONTHLY(1), HOURLY(2)
- `Gender`, `EmploymentGroup`, `ActiveStatus` (có sẵn)
