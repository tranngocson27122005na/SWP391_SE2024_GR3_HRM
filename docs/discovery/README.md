# docs/discovery/

Lớp **Discovery / Knowledge** — hình thành bài toán trước khi viết `docs/modules/`.

## Nguyên tắc

| Loại | Path | Được sửa? |
|------|------|-----------|
| **Nguồn gốc Layer payrol** | `payrol/Layer *` | **Draft for Review v2** (đã viết lại tham số hóa 2026-07-23). Bản Locked 1.1 lưu ý: không dùng làm nguồn sự thật. |
| **Nguồn Locked khác** | File `1.`, `2.` (Infor, NQLD) | **Không** — chỉ đọc / trích dẫn |
| **Bản tổng hợp review** | `00-module-catalog.md`, `payroll/00-uc-catalog.md` | Có — chốt với BA rồi Spec |

Khi chốt Discovery → promote thuật ngữ vào `docs/init/07-glossary.md` → viết 4 file module theo `docs/modules/tmpl/`.

**Cấm trong Discovery:** URL, DDL chi tiết, Controllers, pseudocode.

---

## Mục lục nguồn đã import

| # | File | Artifact | Nội dung chính |
|---|------|----------|----------------|
| 1 | [`1. thông tin về M&T company.docx.md`](./1.%20thông%20tin%20về%20M&T%20company.docx.md) | `M&T-Infor-001` | Bối cảnh công ty, org, bảng duyệt 2 cấp, core workflows |
| 2 | [`2. Nội quy công ty M&T.docx.md`](./2.%20Nội%20quy%20công%20ty%20M&T.docx.md) | `CORP-HR-NQLD-001` | Nội quy lao động (HĐLĐ, giờ công, phép, OT, lương, BHXH…) |
| 3 | [`payrol/Layer 1 — Entity Mapping.docx.md`](./payrol/Layer%201%20—%20Entity%20Mapping.docx.md) | `HRMS-L1.5-COMP-ENT-001` **v2 Draft** | Entity / tham số hóa |
| 4 | [`payrol/Layer 2_ PAYROLL ELEMENTS.md`](./payrol/Layer%202_%20PAYROLL%20ELEMENTS.md) | `HRMS-L2-PAY-ELEMENT-001` **v2 Draft** | Elements + param seed |
| 5 | [`payrol/Layer 3 — RULE ENGINE.md`](./payrol/Layer%203%20—%20RULE%20ENGINE.md) | `HRMS-L3-PAY-RULE-001` **v2 Draft** | Rules tham số hóa; meal = OT_WF × OT_SALARY_WF |

## Bản tổng hợp để review

- [`00-module-catalog.md`](./00-module-catalog.md) — module/workflow tham khảo từ nguồn Locked.
- [`01-mvp-scope.md`](./01-mvp-scope.md) — phạm vi MVP + mô hình phân quyền (**đã align init** qua ADR-0001).
- [`auth/01-login-authorization.md`](./auth/01-login-authorization.md) — đăng nhập + phân quyền (có note wave 2 org).
- [`org/00-uc-catalog.md`](./org/00-uc-catalog.md) — **Hồ sơ & vòng đời theo HĐ** (rev **0.4** — ADR-0004, BR kiểm tra mỗi ngày làm việc).
  - [`org/01-employee-profile.md`](./org/01-employee-profile.md) — UC emp
  - [`org/02-labor-contract.md`](./org/02-labor-contract.md) — UC hợp đồng
  - [`org/03-dependent.md`](./org/03-dependent.md) — NPT
- [`admin/02-sys-user-provision.md`](./admin/02-sys-user-provision.md) — bù TK tay + **BR-ADM-SYNC-01**.
- [`common/01-notification.md`](./common/01-notification.md) — **Deferred**.
- [`payroll/00-uc-catalog.md`](./payroll/00-uc-catalog.md) — Payroll nháp + param (rev **1.0**).
- [`attendance/00-uc-catalog.md`](./attendance/00-uc-catalog.md) — Import `attendance_summary` only (rev **1.0**).
- ADR: [`ADR-0003`](../decisions/ADR-0003-datascope-row-level-only.md) (DataScope row-level); [`ADR-0004`](../decisions/ADR-0004-manual-sys-user-sync-periodic-check.md) (đồng bộ TK tay + kiểm tra định kỳ).
