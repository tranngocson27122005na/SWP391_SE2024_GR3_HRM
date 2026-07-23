# ADR-0004 — Liên kết emp ↔ `sys_user`: thao tác tay có kiểm tra định kỳ (không trigger / không notify)

**Status:** Accepted  
**Date:** 2026-07-23  
**Deciders:** Project team (HRMS M&T)  
**Related:** ADR-0001 (tách realm), `docs/discovery/org/00-uc-catalog.md` (rev ≥ 0.3), `docs/discovery/admin/02-sys-user-provision.md`

---

## Context

Core MVP cần nối **hồ sơ/HĐ (org, USER)** với **tài khoản đăng nhập (admin, ADMIN)** ở hai chiều:

- **Onboard:** emp+HĐ đã có → cần `sys_user` để login.  
- **Offboard:** hết HĐ ACTIVE → cần INACTIVE `sys_user` để không còn truy cập.

Các hướng đã xét (trigger đồng bộ, notification bắt buộc, org tự tạo TK) hoặc **vi phạm realm**, hoặc **chưa có plan exception** đủ tốt, hoặc dễ **chặn nghiệp vụ chính** (rollback tạo NV khi trigger lỗi). Tài liệu MVP hiện ưu tiên **happy path chính xác / đơn giản**, chưa đầu tư pipeline tự động hóa liên realm.

Quy mô ~180 NLĐ; tần suất onboard/offboard thấp → thao tác tay **chấp nhận được** nếu có **business rule kiểm tra định kỳ có tần suất**, tránh “bắt buộc trên giấy / tùy hứng trên thực tế” (đặc biệt rủi ro offboard: TK người đã nghỉ vẫn ACTIVE).

## Decision

1. **Phạm vi:** Áp dụng **cả onboard và offboard**. Hai chiều **không** dùng notification bắt buộc; **không** dùng DB trigger provision / auto khóa TK trong Core MVP này.  
2. **Cơ chế:** Org chỉ đổi emp/HĐ. Mọi tạo / Active / Inactive `sys_user` do **ADMIN thao tác tay** (list bù TK; đổi status).  
3. **Lý do:** Ưu tiên **chính xác ranh giới realm + đơn giản vận hành happy path** hơn tự động hóa ở giai đoạn hiện tại; tránh thiết kế nửa vời gây mâu thuẫn hoặc làm hỏng luồng HRS. Đây là **đánh đổi có chủ đích**, không phải quên làm.  
4. **Business rule kiểm tra định kỳ (bắt buộc):** xem BR-ADM-SYNC trong discovery admin — ADMIN phải kiểm tra **mỗi ngày làm việc** hai danh sách lệch (chi tiết discovery).  
5. **Revisit** khi một trong các điều kiện sau xảy ra (mở lại trigger / notify / job):  
   - Quy mô nhân sự **> ~300** NLĐ đang theo dõi trên hệ thống, **hoặc**  
   - Trung bình onboard **≥ 10** NV/tháng **hoặc** offboard **≥ 5** NV/tháng trong ≥ 2 tháng liên tiếp, **hoặc**  
   - Đã có audit/incident: TK ACTIVE của người đã hết HĐ ACTIVE **> 1 ngày làm việc** mà không được xử lý, **hoặc**  
   - Payroll/offboard đã có chốt kỳ đủ tin cậy để gắn auto khóa TK sau kỳ lương.

## Consequences

### Positive
- Org Spec/code không đụng `sys_user`; dễ test happy path HRS.  
- Không phụ thuộc notify/trigger khi chưa có design exception.  
- Độc giả sau này đọc ADR biết đây là trade-off có chủ đích + điều kiện mở lại.

### Negative / Follow-up
- Phụ thuộc kỷ luật ADMIN theo BR tần suất; nếu bỏ kiểm tra → rủi ro bảo mật offboard.  
- Mitigate: UI list “cần xử lý” rõ trên màn admin (Spec); BR ghi tần suất tường minh.  
- Cập nhật Spec admin khi implement.

## Alternatives considered

| Phương án | Lý do không chọn (Core MVP) |
|-----------|------------------------------|
| DB trigger provision trong TX org | Lỗi trigger rollback tạo NV; bảo trì hash/`user_role` khó |
| Notification bắt buộc hai chiều | Chưa plan exception; thêm module common sớm; dễ hiểu nhầm đã “xong tự động” |
| Org app tạo/khóa `sys_user` | Vi phạm ADR-0001 realm |
| Chỉ “ADMIN làm khi nhớ” không tần suất | “Bắt buộc” thành tùy hứng — loại |

## Notes

Tần suất BR mặc định (**mỗi ngày làm việc**) chọn để giảm cửa sổ TK còn ACTIVE sau offboard. Có thể siết/nới khi revisit — đổi số trong discovery/Spec, không đổi tinh thần ADR trừ khi bỏ hẳn chế độ tay.
