package com.hrm.service.payroll;

import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.persistence.executor.SqlExecutor;
import com.hrm.persistence.entity.PayrollParam;
import com.hrm.persistence.mapper.PayrollParamMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class PayrollParamService {

    public List<PayrollParam> listAll(UserSession user) {
        assertUser(user);
        List<PayrollParam> list = SqlExecutor.execute(PayrollParamMapper.class, PayrollParamMapper::selectAll);
        return list == null ? List.of() : list;
    }

    public void updateValues(UserSession user, Map<String, BigDecimal> updates) {
        assertUser(user);
        if (updates == null || updates.isEmpty()) {
            throw new ValidationException("Không có tham số để cập nhật");
        }
        SqlExecutor.executeTransaction(session -> {
            PayrollParamMapper mapper = session.getMapper(PayrollParamMapper.class);
            for (Map.Entry<String, BigDecimal> e : updates.entrySet()) {
                if (e.getKey() == null || e.getKey().isBlank() || e.getValue() == null) {
                    continue;
                }
                int n = mapper.updateValue(e.getKey().trim(), e.getValue());
                if (n == 0) {
                    throw new ValidationException("Không tìm thấy param: " + e.getKey());
                }
            }
            return null;
        });
    }

    private void assertUser(UserSession user) {
        if (user == null || !user.isUser()) {
            throw new UnauthorizedException("Không có quyền");
        }
    }
}
