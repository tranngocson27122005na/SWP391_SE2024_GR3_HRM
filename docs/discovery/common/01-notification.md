# 01 — Notification (common) — TREO ngoài Core MVP hiện tại

**Topic:** `common` / notification  
**Status:** Deferred (không thuộc wave org Core MVP rev 0.3)  
**Date:** 2026-07-23

## Lý do treo

Wave org cố ý **happy path**: HRS ↔ hồ sơ/HĐ; ADMIN ↔ tài khoản tay.  
Đưa notification / trigger vào khi **chưa** có plan exception rõ → thêm mâu thuẫn và lệch nghiệp vụ chính.

## Khi nào mở lại

Khi đủ điều kiện **Revisit** trong [ADR-0004](../../decisions/ADR-0004-manual-sys-user-sync-periodic-check.md) (quy mô / tần suất / incident / kỳ lương tin cậy), hoặc có UC nghiệp vụ khác (payslip, duyệt đơn) cần kênh in-app riêng.  
**Không** dùng làm cầu nối bắt buộc emp → `sys_user` trong Core MVP hiện tại.

## Tham chiếu thay thế MVP

[`../admin/02-sys-user-provision.md`](../admin/02-sys-user-provision.md) — bù TK tay.
