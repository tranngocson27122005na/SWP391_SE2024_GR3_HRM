# 02-technology-stack.md

**Technology Stack**  
**HRMS for M&T Company**  
**Version:** 1.1.0  
**Date:** 2026-07-21  
**Status:** Draft for Review

## 1. Core Technology

| Layer              | Technology                          | Version | Lý do chọn |
|--------------------|-------------------------------------|---------|----------|
| Language           | Java                                | 17      | Ổn định, hiệu suất cao, phù hợp với doanh nghiệp Việt Nam |
| Build Tool         | Apache Maven                        | 3.9+    | Quản lý dependency rõ ràng, chuẩn công nghiệp |
| Database           | MySQL                               | 8.0+    | Hỗ trợ tốt utf8mb4, dễ triển khai, phù hợp quy mô 180 users |
| ORM / Data Access  | MyBatis                             | 3.5+    | Linh hoạt SQL, dễ debug, kiểm soát transaction chặt chẽ |
| Web Framework      | Servlet + JSP                       | -       | Hiểu rõ request-response lifecycle, không magic |
| Frontend           | JSP + Bootstrap 5 + jQuery          | -       | Đơn giản, không cần build step phức tạp |
| Server             | Apache Tomcat                       | 10.x    | Chuẩn Servlet 6.0, dễ deploy |

## 2. Development Tools

- **IDE**: Cursor (primary) + IntelliJ IDEA
- **Version Control**: Git
- **Database Client**: DBeaver / MySQL Workbench
- **API Testing**: Postman (nếu cần test controller)
- **Documentation**: Markdown + PlantUML (cho diagram)

## 3. Kiến trúc & Thư viện hỗ trợ

- **MVC mở rộng + DDD Lite**: Tổ chức code theo domain và feature.
- **Security**: Custom Servlet Filter + Session Management.
- **Transaction**: MyBatis + manual transaction qua SqlExecutor.
- **Exception Handling**: Custom BusinessException hierarchy.
- **Logging**: SLF4J + Logback.

## 4. Quyết định Không Sử dụng

- **Không dùng Spring Boot/Spring MVC**: Để tập trung nắm vững nền tảng Java Web và tránh overhead không cần thiết cho quy mô dự án.
- **Không dùng JPA/Hibernate**: Ưu tiên kiểm soát SQL trực tiếp qua MyBatis.
- **Không dùng frontend framework nặng** (React, Vue...): Giữ nguyên JSP để phù hợp với mục tiêu học sâu và deploy đơn giản.
- **Không dùng microservices**: Monolith là đủ và phù hợp giai đoạn hiện tại.

## 5. Nguyên tắc Công nghệ

Mọi công nghệ và thư viện được chọn phải đáp ứng:
- Dễ hiểu và dễ bảo trì bởi đội ngũ.
- Hỗ trợ mạnh Documentation-First và AI-Assisted Development.
- Không giới thiệu complexity không cần thiết (YAGNI).
- Đảm bảo tính ổn định và khả năng mở rộng hợp lý.
## Global Constraints

Business terminology

Mọi:

- Entity
- Data Field
- Business Rule
- Workflow
- Role

phải sử dụng đúng định nghĩa trong

docs/init/07-glossary.md

Không được đổi tên.

**Bất kỳ thay đổi nào về technology stack sau này đều phải được ghi nhận trong `decisions/` dưới dạng ADR.**

---