# 09-development-workflow.md

**Development Workflow**  
**HRMS for M&T Company**  
**Version:** 1.4.0  
**Date:** 2026-07-22  
**Status:** Draft for Review

## 1. Quy trình tổng quát

Dự án tuân thủ quy trình **Documentation-Driven Agile**:

1. **Initiation** → Cập nhật `docs/init/` và `docs/backlog/`
2. **Discovery** → Hình thành bài toán trong `docs/discovery/{topic}/` (team tự cập nhật nội dung). Nếu Discovery phát hiện mâu thuẫn với `docs/init/`, cập nhật init theo **Document Priority** (`01-init.md`); thay đổi kiến trúc phải kèm ADR.
3. **Sprint Planning** → Chọn user stories cho Sprint
4. **Specification** → Viết chi tiết trong `docs/modules/{domain}/` theo template `docs/modules/tmpl/`
5. **Implementation** → AI sinh code dựa trên Specification
6. **Review & Sync** → Kiểm tra tính đồng bộ giữa code và tài liệu
7. **Demo & Retrospective** → Kết thúc Sprint

## 2. Quy trình chi tiết cho một Module

1. (Khuyến nghị) Hoàn thành Discovery brief trong `docs/discovery/{topic}/`.
2. Promote thuật ngữ mới vào `docs/init/07-glossary.md` nếu có; phê duyệt trước khi dùng.
3. Copy template từ `docs/modules/tmpl/` vào `docs/modules/{domain}/` (4 file: 01–04).
4. Điền đủ nội dung → phê duyệt Specification.
5. Triển khai từng use case theo thứ tự ưu tiên.
6. Cập nhật Implementation Notes sau khi code hoàn thành.
7. Code Review tập trung vào Architecture, Convention và Specification.

## 3. Git Workflow

- `main`: Nhánh ổn định, chỉ merge khi đã review.
- `develop`: Nhánh tích hợp.
- `feature/{name}`: Nhánh phát triển tính năng.
- `hotfix/{name}`: Sửa lỗi khẩn cấp.

Mọi commit phải tuân thủ Coding Convention.

## 4. Definition of Done (DoD)

Một user story được coi là hoàn thành khi:
- Có Specification đầy đủ trong `docs/modules/`.
- Code được viết theo Architecture và Convention.
- Tích hợp thành công không phá vỡ module khác.
- Đã update Implementation Notes (nếu cần).
- Đã demo theo từng **realm** (ADMIN/USER) và các `job_position` liên quan.
- Tài liệu và code đồng bộ.

## 4b. Definition of Ready (DoR) – Khi nào AI bắt đầu viết code?

Một user story được coi là **Ready** khi:
- (Khuyến nghị) Có Discovery brief đã review trong `docs/discovery/` — team tự duy trì nội dung.
- Đã có Specification (`01-specification.md`) đầy đủ các FR, Actors, Permissions.
- Đã có Database Design (`02-database.md`) với chi tiết bảng, cột, và câu SQL mẫu. **DB-first**: schema thực tế trong `sql/0.hrmdb.sql` (database `hrmdb`) là nguồn; nếu lệch, cập nhật `02-database.md` hoặc tạo migration (+ ADR nếu đổi cấu trúc Core). Cột code/status tuân ADR-0002 (TINYINT + enum).
- Đã có Design (`03-design.md`) với URL pattern, JSP path, Controller class name cụ thể.
- Đã có Implementation (`04-implementation.md`) với pseudocode từng bước và method signature.

**Không** bắt tay vào code nếu chưa có đủ 4 file module (01–04).

## 4c. Quy tắc viết Specification (dành cho AI)

Khi AI được yêu cầu viết Specification cho một module mới, phải tuân thủ:
1. Sử dụng template từ `docs/modules/tmpl/`.
2. Mọi FR đều phải xác định Actor theo **realm** (ADMIN/USER) hoặc **`job_position`** — Core MVP không dùng khái niệm "Role" nghiệp vụ nào khác ngoài `ADMIN`|`USER`.
3. Mọi FR đều phải sinh ra ít nhất 1 Permission theo dạng `{feature}:{ACTION}` (VD: `employee:READ`). ACTION luôn UPPERCASE.
4. Không được đề cập đến DB schema, code, hay framework trong Specification.
5. Tham chiếu thuật ngữ đúng theo `07-glossary.md`.

## 5. Công cụ hỗ trợ

- Cursor + AI để sinh code.
- Git cho version control.
- Markdown cho tất cả tài liệu.
- MySQL + MyBatis cho data.

## 6. Trách nhiệm

- **Business Analyst/Architect**: Duy trì tài liệu `docs/` (gồm Discovery).
- **AI Coding Assistant**: Sinh code đúng Specification.
- **Developer**: Review, test và đảm bảo chất lượng.

**Quy trình này đảm bảo dự án phát triển có kỷ luật, minh bạch và bền vững.**
