# 01-specification.md - {Tên Module}

**Module:** {Module Name}  
**Domain:** `{domain}` (package/JSP; không lên URL)  
**Feature(s):** `{feature}` (kebab-case; một module có thể nhiều feature — liệt kê hết)  
**Discovery ref:** `docs/discovery/{topic}/` (nếu có)  
**Version:** 1.0.0 | **Date:** YYYY-MM-DD | **Status:** Draft

---

## 1. Context
{1–2 câu: mục đích module.}

## 2. Scope
- **In scope**: …
- **Out of scope / Non-goals**: …

## 3. Actors & DataScope

System role chỉ `ADMIN` | `USER` (xem `07-glossary`). Chức danh nghiệp vụ = `job_position`, **không** phải system role.

| Actor | Realm | DataScope áp dụng | Ghi chú |
| :--- | :--- | :--- | :--- |
| **ADMIN** | Hệ thống | — | Chỉ URL/module admin; **không** DataScope nghiệp vụ |
| **USER** + `{position_code}` | Nghiệp vụ | `SELF` / `DEPARTMENT` / `ALL` (từ `job_position.data_scope`) | Liệt kê từng chức danh liên quan FR |

*Module chỉ ADMIN:* bỏ cột DataScope hoặc ghi N/A.  
*Module dùng chung (login, đổi MK, home, 403):* Actor = mọi người đã login / anonymous — **không** gán `position_permission`.

## 4. Feature List & Permissions

Với mỗi `{feature}` nghiệp vụ:

**Actions dùng** (bỏ action không cần — YAGNI):  
`READ`, `CREATE`, `UPDATE`, `DELETE`, `SUBMIT`, `APPROVE`, `REJECT`, `CANCEL`, `IMPORT`, `EXPORT`

| Permission | Gán qua `position_permission` (seed / ADMIN gán) | Ghi chú |
| :--- | :--- | :--- |
| `{feature}:READ` | VD: HR-MGR, FAC-WRK… | |
| `{feature}:CREATE` | … | |

**Không đưa vào bảng permission** (dùng chung / NFR): login, logout, home, đổi MK bản thân, trang lỗi — ghi rõ ở đây nếu module chứa chúng.

**ADMIN-only** (không qua `position_permission`): liệt kê thao tác hệ thống (VD ma trận, list/khóa `sys_user`).

## 5. Functional Requirements
| FR ID | Mô tả | Actor | Permission / cơ chế | Acceptance Criteria |
| :--- | :--- | :--- | :--- | :--- |
| FR-01 | … | USER + HR-MGR / ADMIN / mọi user đã login | `{feature}:READ` hoặc *dùng chung* hoặc *ADMIN realm* | … |

## 6. Business Rules (Service)
*Vi phạm → Service throw `ValidationException` / `BusinessException` (message end-user Tiếng Việt).*
- **BR-01**: …
- **BR-02**: …

## 7. Validation Rules (Controller)
*Vi phạm → forward về form kèm `errorMessage`. **Không** throw ở Controller.*

| Field | Rule | Error Message (Tiếng Việt) |
| :--- | :--- | :--- |
| `…` | Required / Format / Length | "…" |

*Màn hình không form (list/detail/đổi MK đơn giản):* vẫn liệt kê field cần validate hoặc ghi N/A.

## 8. Dependencies
- Core / module khác: …
- Discovery: …
- Thuật ngữ mới → `07-glossary.md` trước khi dùng
- ADR liên quan: …
