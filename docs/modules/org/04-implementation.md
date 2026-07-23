# 04-implementation.md - Org

**Module:** Org  
**Version:** 2.0.0 | **Date:** 2026-07-23 | **Status:** Draft

---

## 1. Packages

- `controller.org.employee|contract|dependent`
- `service.org`: `EmployeeQueryService`, `EmployeeCommandService`, `ContractService`, `DependentService`
- Mapper XML: Employee, Contract, Dependent (+ JobPosition active list)

## 2. Signatures (chính)

```
EmployeeCommandService
  int createWithFirstContract(UserSession, EmployeeFormRequest, ContractFormRequest)  // TX
  void updateProfile(UserSession, Integer id, EmployeeFormRequest)

ContractService
  List<ContractResponse> listByEmployee(UserSession, Integer employeeId)
  void createOrSwitch(UserSession, ContractFormRequest)  // TX: inactive old + insert + set current
  void softDelete(UserSession, Integer contractId)       // TX
  void activate(UserSession, Integer contractId)         // TX via update

DependentService
  list/create/update/softDelete với DataScope SELF hoặc ALL (HRS)
```

SqlExecutor + transaction helper nếu chưa có: bọc nhiều mapper calls trong một SqlSession commit.

## 3. Notes

1. Create emp: insert emp (current null) → insert contract ACTIVE → update emp.current_contract_id — một commit.
2. Không gọi SysUserMapper từ org services.
3. List emp: bỏ filter `e.status=1` bắt buộc; optional filter đang làm = current_contract_id IS NOT NULL.
4. HomeController: tile me → `/employee/me`; bỏ phụ thuộc `employee-self:READ`.
5. AuthFilter: `/employee/me` login-only USER; bỏ nhánh employee-self hoặc giữ tương thích tạm rồi gỡ seed.

## 4. Checklist code

- [ ] Migration SQL chạy được
- [ ] FR-E04 TX emp+HĐ
- [ ] Một HĐ ACTIVE enforce
- [ ] Self `/employee/me` không cần ma trận
- [ ] BUILD SUCCESS
