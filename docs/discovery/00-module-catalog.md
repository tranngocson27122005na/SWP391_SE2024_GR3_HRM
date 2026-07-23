# 00-module-catalog.md — Discovery tổng hợp (Draft for Review)

**Mục đích:** Trả lời “module nào làm gì” và “workflow ra sao” trước khi viết spec/design/impl.  
**Nguồn:** chỉ trích từ tài liệu Locked trong `docs/discovery/` — không suy diễn ngoài nguồn.  
**Status:** Draft for Review với BA  
**Date:** 2026-07-21

---

## 1. Bối cảnh bài toán

| Mục | Nội dung | Nguồn |
|-----|----------|-------|
| Công ty | Sản xuất & thương mại M&T | M&T-Infor-001 |
| Quy mô | 180 NLĐ: 150 công nhân xưởng + 30 văn phòng | M&T-Infor-001; NQLD Điều 1 |
| As-is | Excel (tổ chức, chấm công, lương, hồ sơ) + PDF (HĐLĐ) | M&T-Infor-001 |
| To-be | HRMS tích hợp: hồ sơ nhân sự, chấm công, tính lương + duyệt đơn | M&T-Infor-001 |

**Hai nhóm lao động (bắt buộc cho rule OT/phép):** `OFFICE` | `FACTORY` — Layer 1 §1.1.

---

## 2. Bản đồ module ứng viên

Mỗi dòng = 1 module/domain tiềm năng để sau này có `docs/modules/{domain}/`.

| # | Module (đề xuất domain) | Module làm gì | Có workflow duyệt? | Nguồn chính |
|--:|-------------------------|---------------|--------------------|-------------|
| 0 | **authority** (Core) | Phòng ban, chức vụ, hồ sơ NV, tài khoản, phân quyền, menu | Không (CRUD + soft-delete) | Đã có module docs; org chart M&T-Infor-001 |
| 1 | **contract** / lifecycle | HĐLĐ (thử việc / có hạn / vô hạn), onboarding/offboarding, cảnh báo hết hạn | Một phần (tạo TK, thôi việc) | Layer 1 §1; NQLD Ch.II |
| 2 | **attendance** | Import Excel chấm công, xem công, khiếu nại điều chỉnh, khóa công | Có — Attendance Request | M&T-Infor-001 WF1; Layer 1 §2 |
| 3 | **leave** | Phép năm / không lương, số dư phép theo khối + thâm niên | Có — Leave Request | NQLD Điều 9–10; Layer 1 §2.3 |
| 4 | **overtime** | Xin OT, giới hạn giờ, hệ số 150/200/300% (+ đêm) | Có — Overtime Request | NQLD Điều 11–12; Layer 1 §3 |
| 5 | **payroll** | Kỳ lương, rule engine, payslip, tham số hệ thống | Có — khóa công → tính → duyệt lương | Layer 1–3; M&T-Infor-001 WF1 |
| 6 | **promotion** | Thăng / giảm chức | Có — Promotion Request | M&T-Infor-001; NQLD Điều 25 |
| 7 | **resignation** | Xin thôi việc + nghĩa vụ báo trước | Có — Resignation (1 cấp HRS) | M&T-Infor-001; NQLD Điều 4–5 |

**Ngoài MVP / ghi chú nguồn:** Thưởng lễ Tết riêng — Layer 1 §3.3 *“chưa có chính sách nên không làm”*.

---

## 3. Workflow duyệt đơn (chuẩn 2 cấp)

Nguồn: M&T-Infor-001 bảng Request + NQLD Điều 25.

| Workflow | Việc cần làm | Initiator | Approver Lv1 | Approver Lv2 |
|----------|--------------|-----------|--------------|--------------|
| **Attendance Request** | Xin điều chỉnh / khiếu nại công | NV, Quản lý | Trưởng bộ phận | HRS |
| **Leave Request** | Xin nghỉ phép | NV, Quản lý | HRS | HRM |
| **Overtime Request** | Xin tăng ca | NV, Quản lý | Trưởng bộ phận* | HRS |
| **Promotion Request** | Thăng / giảm chức | HRS | HRM | Director / CEO |
| **Resignation Request** | Xin nghỉ việc | NV, Quản lý | HRS | — (1 cấp) |

\* Khối sản xuất: Quản đốc xưởng = Trưởng bộ phận duyệt OT (M&T-Infor-001).

### Chuỗi trạng thái gợi ý (chưa chốt enum — chỉ Discovery)

`DRAFT → SUBMITTED → APPROVED_L1 → APPROVED_L2 / REJECTED → (CANCELLED)`  
Resignation có thể rút gọn: `DRAFT → SUBMITTED → APPROVED / REJECTED`.

---

## 4. Workflow lõi: Chấm công → Lương

Nguồn: M&T-Infor-001 §Core Workflows.

```
[1] HRS/C&B import Excel công từ máy chấm công
        ↓
[2] NV / công nhân xem công trên HRMS
        ↓
[3] (Nếu sai) Attendance Request → duyệt 2 cấp → cập nhật công
        ↓
[4] HR khóa công (Lock Timesheet) — dữ liệu bất biến trước lương
        ↓
[5] Tính lương (Payroll Run / Rule Engine) cho 180 NV
        ↓
[6] HRM kiểm tra (BHXH, phụ cấp, thuế…) → gửi CEO duyệt danh sách lương
        ↓
[7] NV xem Payslip (ngày trả lương: mùng 10 tháng sau — NQLD Điều 14)
```

---

## 5. Workflow theo từng module (tóm tắt “làm gì”)

### 5.1 Authority (đã có code/docs)
- CRUD department, job-position, employee, user, permission-matrix.
- Không duyệt 2 cấp.
- Nền cho mọi module khác (identity + RBAC + DataScope).

### 5.2 Employee & Contract
- Quản lý hồ sơ + HĐLĐ thay PDF; nhập tay trên hệ thống.
- Onboarding: HR tạo hồ sơ → tạo tài khoản.
- Offboarding: chuyển trạng thái nghỉ việc; chốt sổ BHXH trong **14 ngày** (NQLD Điều 16 / Infor).
- Thử việc: lương ≥ **85%** lương chính thức; hạn thử việc theo vị trí (NQLD Điều 3).

### 5.3 Attendance
- Import Excel In/Out → bản ghi công ngày.
- Self-service xem công.
- Attendance Request khi sai sót.
- **Khóa công** trước khi chạy payroll.

### 5.4 Leave
- Loại: phép năm (12 ngày OFFICE / 14 ngày FACTORY + 1 ngày / 5 năm thâm niên), nghỉ riêng có/không lương, không lương thỏa thuận.
- Leave Request: HRS → HRM.
- Phép tồn khi thôi việc được thanh toán lương (NQLD Điều 9).

### 5.5 Overtime
- Bắt buộc có đồng ý NLĐ + duyệt OT Request.
- Giới hạn: ≤ 4h/ngày (50%), ≤ 40h/tháng, ≤ 200h/năm (đặc biệt tới 300h — NQLD Điều 11).
- OFFICE: không OT ngày thường trong giờ hành chính; FACTORY: OT 2h hoặc 4h ngày thường; cả hai khối: OT cuối tuần / lễ.
- Hệ số: 150% / 200% / 300%; đêm +30%; đêm đồng thời +20% (NQLD Điều 12) — số cụ thể nằm Layer 2 tham số động.

### 5.6 Payroll
- Input: công đã khóa + OT đã duyệt + hợp đồng/probation + dependents (PIT) + elements Layer 2.
- Engine: atomic rules theo thứ tự Layer 3 → aggregation → NetPay.
- Output: PayrollDetail, Payslip; ADMIN/HR chạy kỳ; DIRECTOR/CEO đọc báo cáo; EMPLOYEE đọc SELF.

### 5.7 Promotion / Resignation
- Promotion: HRS khởi tạo → HRM → Director.
- Resignation: báo trước 45/30/3 ngày theo loại HĐ (NQLD Điều 4); HRS duyệt 1 cấp.

---

## 6. Org / Actor trong Discovery vs Role trong `07-glossary`

| Actor trong Discovery (Infor + NQLD) | Role hiện tại trong init glossary | Ghi chú để chốt |
|-------------------------------------|-----------------------------------|-----------------|
| CEO / Giám đốc | DIRECTOR | Cần thống nhất tên |
| HRM (Trưởng phòng NS) | ? (chưa có) | Có thể map ADMIN hoặc role HR mới |
| HRS / C&B (HR Staff) | ? | Role vận hành import công + tính lương |
| Trưởng bộ phận / Quản đốc | MANAGER | Scope theo department |
| NV văn phòng / Công nhân | EMPLOYEE | Phân nhánh bằng `Employee_Group`, không cần 2 role |

**Điểm mở (phải chốt trước khi viết module approval):**  
Glossary hiện chỉ có `ADMIN | DIRECTOR | MANAGER | EMPLOYEE`. Discovery dùng `HRS | HRM | CEO`.  
→ Hoặc **mở rộng Role List** trong glossary, hoặc **ánh xạ** HRS/HRM → ADMIN (mất tách quyền C&B vs Trưởng phòng).

---

## 7. Phụ thuộc giữa module (thứ tự gợi ý)

```
authority
   → contract / employee lifecycle
   → attendance  ↔  leave  ↔  overtime
   → payroll (phụ thuộc công đã khóa + OT + HĐLĐ + params)
   → promotion / resignation (có thể song song sau authority)
```

---

## 8. Câu hỏi review với BA (chốt bản cuối)

1. MVP Sprint gần nhất gồm những module nào trong bảng §2? (Đề xuất tối thiểu: authority đã có → attendance → payroll; leave/OT có thể cùng phase hoặc phase sau.)
2. Chốt **Role model**: giữ 4 role init và map HRS/HRM thế nào, hay thêm role?
3. Promotion / Resignation có vào MVP không, hay chỉ authority + attendance + leave + OT + payroll?
4. `Employee_Group` OFFICE/FACTORY: field trên employee hay suy từ department?
5. Duyệt lương cấp CEO: nằm trong module `payroll` hay workflow approval generic?

---

## 9. Trạng thái bước tiếp

| Bước | Ai | Trạng thái |
|------|-----|------------|
| Import nguồn Locked | Bạn | Done |
| Catalog này | AI | Draft — chờ bạn review |
| Chốt Q1–Q5 | Bạn + AI | Pending |
| Viết 4 file module theo tmpl | AI | Sau khi chốt |
| Code | AI | Sau khi module docs approved |

**Tài liệu Locked gốc không bị sửa.** Mọi chỉnh sửa nghiệp vụ sau review ghi vào bản catalog này hoặc ADR — không ghi đè file `1.` / `2.` / `payrol/Layer *`.
