# 03-design.md — Attendance

**Version:** 1.0.0

| Method | URL | Controller | Permission |
| --- | --- | --- | --- |
| GET+POST | `/attendance/import` | ImportAttendanceController | `attendance:IMPORT` |
| GET | `/attendance/list` | ListAttendanceSummaryController | `attendance:READ` |

Package: `com.hrm.controller.attendance`  
JSP: `/WEB-INF/views/attendance/`  
Service: `com.hrm.service.attendance.AttendanceImportService`
