# ADR-0003 — DataScope là row-level, không field-level redaction

**Status:** Proposed  
**Date:** 2026-07-23  
**Deciders:** Project team (HRMS M&T)  
**Related:** ADR-0001, `docs/init/04-architecture.md` (DataScope), `docs/discovery/org/00-uc-catalog.md`

---

## Context

`job_position.data_scope` (`SELF` | `DEPARTMENT` | `ALL`) quyết định **tập bản ghi** USER được đọc/ghi trong nghiệp vụ (employee, contract, …).

Khi FAC-SUP (DEPARTMENT) được `contract:READ`, họ thấy toàn bộ field của HĐ trong phòng — gồm **mức lương**. Không có lớp ẩn/che field theo vai trò.

Payroll và module sau dễ giả định “đã có ẩn lương” nếu không ghi nhận giới hạn này.

## Decision

1. DataScope MVP = **row-level only**: lọc theo emp / phòng / all.  
2. **Không** triển khai field-level redaction (ẩn `basic_salary`, MST, … theo permission phụ).  
3. Ai có quyền READ trên resource trong scope thì thấy **đủ cột** nghiệp vụ của bản ghi đó.  
4. Chấp nhận FAC-SUP thấy lương HĐ trong phòng là hệ quả của (1)+(3) — đã xác nhận BA cho MVP org.  
5. Mọi module dùng DataScope (org, attendance, payroll, …) tuân cùng giới hạn trừ khi ADR mới thay thế.

## Consequences

### Positive
- Đơn giản Filter/Service/Mapper; một khái niệm scope.  
- Tránh ma trận “permission × field” phình MVP.

### Negative / Follow-up
- Rò rỉ mức lương trong scope DEPARTMENT/ALL nếu gán READ rộng.  
- Mitigate bằng **seed/ma trận chặt** (ai được `contract:READ`), không bằng ẩn cột.  
- Revisit khi có yêu cầu compliance (che lương với giám sát, audit field-level).

## Alternatives considered

| Phương án | Lý do loại (MVP) |
|-----------|------------------|
| DTO/view khác nhau theo role (ẩn salary) | Phức tạp; dễ lệch list vs detail |
| Permission `contract:READ_SALARY` riêng | Thêm chiều ma trận; chưa có nhu cầu tách |
| Chỉ HRS được `contract:READ`; FAC-SUP không xem HĐ | Mâu thuẫn nhu cầu giám sát xem HĐ phòng |

## Revisit khi

- Có yêu cầu pháp lý / nội bộ cấm giám sát xem lương, hoặc  
- Payroll self-service đòi che field trên cùng API list đang dùng chung DataScope.
