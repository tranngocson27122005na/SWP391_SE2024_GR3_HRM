package com.hrm.service.payroll;

import org.apache.ibatis.session.SqlSession;

import com.hrm.persistence.mapper.PayrollBatchMapper;

/**
 * Xóa toàn bộ kết quả payroll (batch cascade payslip/detail/snapshot).
 */
public class PayrollCleanupService {

    public void deleteAllPayrollResults(SqlSession session) {
        // WHERE trên PK để tránh sql_safe_updates (nếu bật trên server)
        session.getMapper(PayrollBatchMapper.class).deleteAll();
    }

    public void deleteAllPayrollResults() {
        com.hrm.infrastructure.persistence.executor.SqlExecutor.executeTransaction(session -> {
            deleteAllPayrollResults(session);
            return null;
        });
    }
}
