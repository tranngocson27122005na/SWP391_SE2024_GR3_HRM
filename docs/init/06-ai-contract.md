# 06-ai-contract.md

**AI Development Contract**  
**HRMS for M&T Company**  
**Version:** 1.1.0  
**Date:** 2026-07-21  
**Status:** Draft for Review

## 1. Nguyên tắc cốt lõi

Tài liệu `docs/` là **Single Source of Truth**.  
Mọi mã nguồn được sinh bởi AI **phải** tuân thủ nghiêm ngặt các tài liệu này. Source code không được coi là nguồn chân lý.

## 2. Trách nhiệm của AI

- **Chỉ sinh code** dựa trên tài liệu đã được phê duyệt trong `docs/modules/{domain}/`.
- **Không suy diễn** yêu cầu nghiệp vụ nếu chưa có trong Specification.
- **Tuân thủ 100%** kiến trúc, convention và principles đã định nghĩa trong `docs/init/`.
- **Báo cáo rõ ràng** nếu phát hiện mâu thuẫn hoặc thiếu sót trong tài liệu.
- **Giữ tính đơn giản** (YAGNI) và tuân thủ Module Independence.

## 3. Quy trình làm việc với AI

1. (Khuyến nghị) Review Discovery brief trong `docs/discovery/` — team tự cập nhật nội dung.
2. Cập nhật / phê duyệt Specification trong `docs/modules/` (template: `docs/modules/tmpl/`).
3. Yêu cầu AI sinh code cho một use case cụ thể.
4. AI sinh code + cập nhật Implementation Notes (nếu cần).
5. Kiểm tra tính tương thích với Architecture và Convention.
6. Merge chỉ khi tất cả tài liệu và code đồng bộ.

## 4. Cấm

- Sinh code mà không có Specification tương ứng.
- Thay đổi kiến trúc hoặc convention mà không cập nhật `decisions/`.
- Thêm thư viện hoặc công nghệ mới mà không có ADR.
- Viết comment kiểu "TODO" hoặc code tạm trong production.
- Sử dụng magic hoặc shortcut vi phạm nguyên tắc minh bạch.
## 4b. Giới hạn suy luận (Sufficiency Rule)

AI **không được**:
- Tạo ra Controller / Service / Mapper chưa có trong `04-implementation.md`.
- Tạo thêm URL mapping ngoài danh sách đã định nghĩa trong `03-design.md`.
- Thêm validation rule không được ghi trong `01-specification.md`.
- Tự động thêm các chức năng "phụ trợ" như export, import, search nếu không được yêu cầu rõ ràng.
- Thay đổi tên method, class, package đã được chỉ định trong implementation notes.

**Nếu phát hiện thiếu sót** trong tài liệu (VD: thiếu validation rule), AI phải:
1. Dừng việc sinh code.
2. Báo cáo rõ ràng: "Thiếu thông tin tại mục ... trong file ...".
3. Chờ được bổ sung tài liệu trước khi tiếp tục.
## 5. Cam kết

AI cam kết hỗ trợ phát triển theo triết lý **Documentation First**, **Specification First**, và **AI-Assisted Development** một cách kỷ luật và minh bạch.

Mọi vi phạm hợp đồng này sẽ được ghi nhận và có thể dẫn đến việc điều chỉnh prompt hoặc quy trình.

**Hợp đồng này có hiệu lực suốt vòng đời dự án.**

---