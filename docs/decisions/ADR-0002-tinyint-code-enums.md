# ADR-0002 — TINYINT code columns + Java enums (no DB CHECK lists)

**Status:** Accepted  
**Date:** 2026-07-22  
**Deciders:** Project team (HRMS M&T)  
**Related:** `sql/0.hrmdb.sql`, `docs/init/04-architecture.md`, `docs/init/05-coding-convention.md`, `docs/init/07-glossary.md`, ADR-0001

---

## Context

Schema thực tế: `sql/0.hrmdb.sql` (database **`hrmdb`**).

Cần lưu trạng thái / loại / action mà **không gắn chặt semantics vào DB** (không VARCHAR liệt kê, không MySQL ENUM, không CHECK list). Mục tiêu mở rộng: thêm giá trị mới chỉ sửa enum ở code + seed/UI khi cần.

## Decision

1. **DB chỉ khai báo kiểu `TINYINT`** cho cột code / status / action / type / scope. Không quy định trong schema “số nào = nghĩa nào”.
2. **Số cụ thể (1, 2, 3…) là tùy ý** — chọn trong enum Java sao cho ổn định trong vòng đời dữ liệu; có thể không tuần tự; khoảng trống để mở rộng sau. **Init docs không khóa bảng map số → tên.**
3. **SSoT nghĩa = Java enum** (`int code`, `getCode()`, `fromCode(int)`). Service validate trước ghi. DB là kho số.
4. **`permission.action`**: `TINYINT NOT NULL`. **`permission_name`** vẫn chuỗi `{resource}:{ACTION}` (đọc-được cho seed/UI/log).
5. **Bỏ mọi `CHECK (... IN (...))` liệt kê giá trị** trên Core schema. Chỉ giữ kiểu, NOT NULL/DEFAULT, UNIQUE, FK.
6. **Mọi soft-delete `status`**: TINYINT + enum `ActiveStatus` ở backend (kể cả khi chỉ 2 giá trị).
7. **Vị trí enum (khi refactor code):** technical (`ActiveStatus`, `ScopeType`…) ở `utility/enums` hoặc `infrastructure/enums`; business ở `persistence/entity/enums/`.
8. **Seed SQL** dùng các số mà enum hiện tại quy định (comment trỏ enum / ADR) — không coi dãy số trong seed là hợp đồng bất biến của init.

## Non-goals

- Không bắt buộc dãy code tuần tự 1..N trong tài liệu init.
- Không bắt DB “tự mô tả” semantics bằng CHECK/VARCHAR.

## Consequences

### Positive
- Mở rộng linh hoạt: thêm hằng enum + (tuỳ chọn) dòng seed — không ALTER CHECK.
- Docs init gọn: chỉ nói “TINYINT + enum code”.

### Negative / Follow-up
- Đọc SQL thuần cần mở enum để hiểu số.
- Service bắt buộc validate; code hiện tại chưa đủ enum `code` → refactor sau.

## Alternatives considered

| Phương án | Lý do loại |
|-----------|------------|
| VARCHAR + CHECK tên action | Gắn ngôn ngữ vào DB |
| MySQL ENUM | Khó port / khó đồng bộ Java |
| Khóa bảng số cố định trong init (1=CREATE…) | Trái mục tiêu “TINYINT để dễ mở rộng; số cụ thể kệ enum” |

## Migration note

Reset DB: `0.hrmdb.sql` + `0.seed_data.sql`. Data cũ VARCHAR `action` → chuyển theo enum đang dùng lúc migrate.
