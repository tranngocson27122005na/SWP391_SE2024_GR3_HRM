# 08-project-principles.md

**Project Principles**  
**HRMS for M&T Company**  
**Version:** 1.0.0  
**Date:** 2026-07-10  
**Status:** Draft for Review

## 1. Triết lý cốt lõi

- **Documentation First**: Tài liệu `docs/` luôn là nguồn chân lý. Code sinh sau và phải đồng bộ.
- **Specification First**: Mọi tính năng phải có đặc tả rõ ràng trước khi triển khai.
- **Core MVP First**: Tập trung hoàn thành nền tảng vững chắc trước khi mở rộng.
- **Simple First**: Ưu tiên giải pháp đơn giản, rõ ràng, dễ hiểu trước khi tối ưu.
- **YAGNI**: Không triển khai tính năng chưa cần thiết.
- **Convention over Configuration**: Giảm thiểu config không cần thiết thông qua convention nghiêm ngặt.

## 2. Nguyên tắc kiến trúc

- **Module Independence**: Mỗi domain/module có thể phát triển gần như độc lập.
- **Separation of Concerns**: Controller chỉ định tuyến, Service chứa business logic, Persistence chỉ truy vấn dữ liệu.
- **Transparency**: Tránh magic. Mọi luồng request-response phải dễ theo dõi.
- **Testability**: Code phải dễ viết unit test, đặc biệt Service và Rule Engine.

## 3. Nguyên tắc phát triển

- **AI-Assisted Development**: AI là công cụ hỗ trợ mạnh mẽ nhưng phải tuân thủ hợp đồng và tài liệu.
- **Agile Discipline**: Làm việc theo Sprint, tập trung hoàn thành giá trị trong mỗi iteration.
- **Continuous Documentation**: Cập nhật tài liệu song song với code.
- **Code Ownership**: Mọi thành viên chịu trách nhiệm duy trì chất lượng và tính nhất quán.

## 4. Nguyên tắc chất lượng

- **Readability over Cleverness**: Code phải dễ đọc hơn là thông minh.
- **Consistency**: Tuân thủ convention và architecture ở mọi nơi.
- **Traceability**: Mọi quyết định thiết kế đều có lý do và được ghi nhận.

## 5. Cam kết dự án

Toàn bộ đội ngũ và AI cam kết tuân thủ các nguyên tắc này trong suốt vòng đời dự án.  
Vi phạm nguyên tắc sẽ được ưu tiên xử lý trước khi tiếp tục phát triển tính năng mới.

**Đây là nền tảng văn hóa phát triển của dự án HRMS.**

---