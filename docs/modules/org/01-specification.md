# 01-specification.md - Org (Hồ sơ, HĐ, NPT)

**Module:** Org  
**Domain:** `org`  
**Feature(s):** `employee`, `contract`, `dependent`  
**Discovery ref:** `docs/discovery/org/` (rev ≥ 0.4), ADR-0004  
**Depends on:** common-auth; admin (bù TK tay — ngoài module)  
**Version:** 2.0.0 | **Date:** 2026-07-23 | **Status:** Draft  
**ADR:** ADR-0001, ADR-0002, ADR-0003, ADR-0004

---

## 1. Context

Quản lý hồ sơ nhân viên, hợp đồng (vòng đời đang làm/nghỉ), và người phụ thuộc — phục vụ input tính lương. Org **không** tạo/khóa `sys_user` (ADR-0004).

## 2. Scope

**In scope:**
- Employee: list (đang làm + đã nghỉ), detail, create (+ HĐ đầu cùng TX), update hồ sơ.
- Self-profile: xem hồ sơ mình — **dùng chung** (whitelist), không ma trận.
- Contract: list theo emp, create/switch, DELETE→INACTIVE, activate (UPDATE status).
- Dependent: self CRUD mỏng + INACTIVE; HRS xem/INACTIVE trên ngữ cảnh emp.

**Out:** `employee:DELETE`; CRUD dept/job-position; trigger/notify provision TK; EXPORT; pro-rate giữa kỳ; cảnh báo hết hạn HĐ; duyệt NPT.

## 3. Actors & DataScope

| Actor | Realm | DataScope | Việc |
| :--- | :--- | :--- | :--- |
| HR-MGR / HR-STF | USER | ALL | CUD emp/HĐ; NPT xem/INACTIVE |
| FAC-SUP | USER | DEPARTMENT | READ emp/HĐ (kể cả lương — ADR-0003) |
| FAC-WRK / USER khác | USER | SELF | Self-profile; `contract:READ` / NPT mình |
| Mọi USER có employeeId | USER | — | Self-profile dùng chung |
| ADMIN | ADMIN | — | 403 URL org |

**Đang làm** = `current_contract_id IS NOT NULL` (HĐ đó ACTIVE). **Đã nghỉ** = null.

## 4. Permissions

| Permission | Gán | Ghi chú |
| :--- | :--- | :--- |
| `employee:READ` | HR-MGR, HR-STF, FAC-SUP | List/detail theo scope |
| `employee:CREATE` / `UPDATE` | HR-MGR, HR-STF | Không DELETE |
| `contract:READ` | HR-MGR, HR-STF, FAC-SUP, FAC-WRK | |
| `contract:CREATE` / `UPDATE` / `DELETE` | HR-MGR, HR-STF | DELETE = soft INACTIVE |
| `dependent:READ` / `CREATE` / `UPDATE` / `DELETE` | Self + HRS (seed) | DELETE = INACTIVE |

**Dùng chung (không ma trận):** self-profile.

## 5. Functional Requirements

| FR | Mô tả | Permission / cơ chế | Acceptance |
| :--- | :--- | :--- | :--- |
| FR-E01 | List emp | `employee:READ` | DataScope; cả đang làm + đã nghỉ; keyword; paging |
| FR-E02 | Detail emp | `employee:READ` | Ngoài scope → 403; hiện HĐ hiện hành + số NPT |
| FR-E03 | Self-profile | Dùng chung | Chỉ emp phiên |
| FR-E04 | Tạo emp + HĐ đầu | CREATE emp+contract | Một TX org; không tạo sys_user |
| FR-E05 | Sửa hồ sơ | `employee:UPDATE` | Không đổi mã NV; không đụng vòng đời HĐ |
| FR-C01 | List/detail HĐ theo emp | `contract:READ` | Scope theo emp |
| FR-C02 | Tạo/switch HĐ | `contract:CREATE` | Cũ→INACTIVE; mới ACTIVE; cập nhật current; không đụng TK |
| FR-C03 | Ngừng HĐ | `contract:DELETE` | →INACTIVE; nếu hiện hành → current null |
| FR-C04 | Activate HĐ | `contract:UPDATE` | Switch hiện hành; không đụng TK |
| FR-D01 | Self NPT | dependent * | SELF only |
| FR-D02 | HRS NPT | dependent * | Xem/INACTIVE theo emp |
| FR-H01 | Home | — | Tile Nhân viên nếu READ; Hồ sơ cá nhân nếu có employeeId |

## 6. Business Rules

- **BR-01**: DataScope trước COUNT/LIMIT.
- **BR-02**: Không dùng `employee.status` cho đang làm/nghỉ; cột có thể luôn ACTIVE khi insert.
- **BR-03**: Tối đa một contract ACTIVE / emp; `current_contract_id` đồng bộ trong cùng TX.
- **BR-04**: Loại HĐ: PROBATION \| OFFICIAL; thử việc → payroll ×0.85 (engine sau).
- **BR-05**: Org không INSERT/UPDATE `sys_user` / `user_role`.
- **BR-06**: Switch (CREATE) không cần DELETE; side-effect INACTIVE HĐ cũ.
- **BR-07**: Ngoài scope → UnauthorizedException → 403.
- **BR-08**: Mã NV unique; không đổi sau tạo.

## 7. Validation (Controller → form errorMessage)

| Field | Rule | Message |
| :--- | :--- | :--- |
| employeeCode | Required, max 20 | "Mã nhân viên không hợp lệ" |
| fullName | Required, max 100 | "Họ tên không được để trống" |
| gender / employmentGroup / positionId | Required, enum/tồn tại | "… không hợp lệ" |
| joiningDate / startDate | Required, date | "Ngày không hợp lệ" |
| contractType | 1\|2 | "Loại hợp đồng không hợp lệ" |
| basicSalary | Required, > 0 | "Mức lương không hợp lệ" |
| salaryType | Required enum | "Hình thức lương không hợp lệ" |
| id | Required số | "… không hợp lệ" |

## 8. Dependencies

Discovery org 0.4; ADR-0003/0004; admin BR-ADM-SYNC-01; schema `employee`, `contract`, `dependent` (mới), `current_contract_id`.
