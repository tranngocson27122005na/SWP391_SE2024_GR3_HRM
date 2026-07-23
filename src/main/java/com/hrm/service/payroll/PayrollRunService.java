package com.hrm.service.payroll;

import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.BusinessException;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.persistence.executor.SqlExecutor;
import com.hrm.persistence.entity.AttendanceSummary;
import com.hrm.persistence.entity.Contract;
import com.hrm.persistence.entity.Employee;
import com.hrm.persistence.entity.PayrollBatch;
import com.hrm.persistence.entity.PayrollBatchParam;
import com.hrm.persistence.entity.PayrollBatchPitBracket;
import com.hrm.persistence.entity.PayrollElement;
import com.hrm.persistence.entity.PayrollParam;
import com.hrm.persistence.entity.Payslip;
import com.hrm.persistence.entity.PayslipDetail;
import com.hrm.persistence.entity.enums.ActiveStatus;
import com.hrm.persistence.mapper.AttendanceSummaryMapper;
import com.hrm.persistence.mapper.ContractMapper;
import com.hrm.persistence.mapper.DependentMapper;
import com.hrm.persistence.mapper.EmployeeMapper;
import com.hrm.persistence.mapper.PayrollBatchMapper;
import com.hrm.persistence.mapper.PayrollBatchParamMapper;
import com.hrm.persistence.mapper.PayrollBatchPitBracketMapper;
import com.hrm.persistence.mapper.PayrollElementMapper;
import com.hrm.persistence.mapper.PayrollParamMapper;
import com.hrm.persistence.mapper.PayslipDetailMapper;
import com.hrm.persistence.mapper.PayslipMapper;
import com.hrm.service.payroll.engine.PayrollEngine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayrollRunService {

    private final PayrollCleanupService cleanupService = new PayrollCleanupService();
    private final PayrollEngine engine = new PayrollEngine();

    public RunResult run(UserSession user, int periodMonth, int periodYear) {
        assertUser(user);
        if (periodMonth < 1 || periodMonth > 12 || periodYear < 2000) {
            throw new ValidationException("Kỳ lương không hợp lệ");
        }

        return SqlExecutor.executeTransaction(session -> {
            List<AttendanceSummary> summaries = session.getMapper(AttendanceSummaryMapper.class)
                    .selectByPeriod(periodYear, periodMonth);
            if (summaries == null || summaries.isEmpty()) {
                throw new ValidationException("Chưa có attendance summary cho kỳ "
                        + periodMonth + "/" + periodYear);
            }

            List<PayrollParam> paramRows = session.getMapper(PayrollParamMapper.class).selectAll();
            Map<String, BigDecimal> paramMap = PayrollEngine.toParamMap(paramRows);
            List<PayrollEngine.PitBracket> brackets = PayrollEngine.buildPitBrackets(paramMap);

            List<PayrollElement> elements = session.getMapper(PayrollElementMapper.class).selectAll();
            Map<String, Integer> elementIds = new HashMap<>();
            for (PayrollElement el : elements) {
                elementIds.put(el.getElementCode(), el.getElementId());
            }
            for (String code : List.of("BASE_SALARY", "SENIORITY_ALLOW", "MEAL_ALLOW_FIXED",
                    "MEAL_ALLOW_SURP", "OT_SALARY_WE", "OT_SALARY_HOL", "TIME_DEDUCT", "BH_EMP", "PIT_TAX")) {
                if (!elementIds.containsKey(code)) {
                    throw new ValidationException("Thiếu payroll_element: " + code);
                }
            }

            cleanupService.deleteAllPayrollResults(session);

            PayrollBatch batch = new PayrollBatch();
            batch.setBatchName("Payroll " + periodMonth + "/" + periodYear);
            batch.setPeriodMonth(periodMonth);
            batch.setPeriodYear(periodYear);
            Integer importId = summaries.stream()
                    .map(AttendanceSummary::getImportId)
                    .filter(id -> id != null)
                    .findFirst()
                    .orElse(null);
            batch.setImportId(importId);
            batch.setRunAt(new Date());
            batch.setRunBy(user.getUserId() == null ? null : user.getUserId().intValue());
            batch.setBatchStatus((byte) 1);
            batch.setTotalNet(BigDecimal.ZERO);
            batch.setStatus(ActiveStatus.ACTIVE.toByte());
            session.getMapper(PayrollBatchMapper.class).insert(batch);

            List<PayrollBatchParam> snapParams = new ArrayList<>();
            for (PayrollParam p : paramRows) {
                PayrollBatchParam sp = new PayrollBatchParam();
                sp.setBatchId(batch.getBatchId());
                sp.setParamCode(p.getParamCode());
                sp.setParamValue(p.getParamValue());
                snapParams.add(sp);
            }
            if (!snapParams.isEmpty()) {
                session.getMapper(PayrollBatchParamMapper.class).insertBatch(snapParams);
            }

            List<PayrollBatchPitBracket> snapBrackets = new ArrayList<>();
            for (PayrollEngine.PitBracket b : brackets) {
                PayrollBatchPitBracket row = new PayrollBatchPitBracket();
                row.setBatchId(batch.getBatchId());
                row.setBracketLevel(b.getLevel());
                row.setLowerBound(b.getLowerBound());
                row.setUpperBound(b.getUpperBound());
                row.setRate(b.getRate());
                snapBrackets.add(row);
            }
            session.getMapper(PayrollBatchPitBracketMapper.class).insertBatch(snapBrackets);

            EmployeeMapper empMapper = session.getMapper(EmployeeMapper.class);
            ContractMapper contractMapper = session.getMapper(ContractMapper.class);
            DependentMapper dependentMapper = session.getMapper(DependentMapper.class);
            PayslipMapper payslipMapper = session.getMapper(PayslipMapper.class);
            PayslipDetailMapper detailMapper = session.getMapper(PayslipDetailMapper.class);

            List<String> skipped = new ArrayList<>();
            BigDecimal totalNet = BigDecimal.ZERO;
            int created = 0;

            for (AttendanceSummary summary : summaries) {
                Employee emp = empMapper.selectByPrimaryKey(summary.getEmployeeId());
                if (emp == null) {
                    skipped.add("summary#" + summary.getSummaryId() + ": không tìm thấy NV");
                    continue;
                }
                if (emp.getCurrentContractId() == null) {
                    skipped.add(emp.getEmployeeCode() + ": thiếu current_contract_id");
                    continue;
                }
                Contract contract = contractMapper.selectByPrimaryKey(emp.getCurrentContractId());
                if (contract == null) {
                    skipped.add(emp.getEmployeeCode() + ": không tìm thấy HĐ");
                    continue;
                }

                int deps = dependentMapper.countActiveByEmployeeId(emp.getEmployeeId());
                PayrollEngine.EmpCalcResult calc;
                try {
                    calc = engine.calculate(emp, contract, summary, paramMap, deps,
                            periodMonth, periodYear);
                } catch (BusinessException e) {
                    skipped.add(emp.getEmployeeCode() + ": " + e.getMessage());
                    continue;
                }

                Payslip slip = new Payslip();
                slip.setBatchId(batch.getBatchId());
                slip.setEmployeeId(emp.getEmployeeId());
                slip.setContractId(contract.getContractId());
                slip.setPayslipStatus((byte) 1);
                slip.setNetPay(calc.getNetPay());
                slip.setStatus(ActiveStatus.ACTIVE.toByte());
                payslipMapper.insert(slip);

                for (Map.Entry<String, BigDecimal> entry : calc.elementAmounts().entrySet()) {
                    Integer elementId = elementIds.get(entry.getKey());
                    if (elementId == null) {
                        throw new ValidationException("Thiếu element map: " + entry.getKey());
                    }
                    PayslipDetail d = new PayslipDetail();
                    d.setPayslipId(slip.getPayslipId());
                    d.setElementId(elementId);
                    d.setAmount(entry.getValue() == null ? BigDecimal.ZERO : entry.getValue());
                    if ("PIT_TAX".equals(entry.getKey())) {
                        d.setRemark("NPT=" + deps);
                    }
                    detailMapper.insert(d);
                }

                totalNet = totalNet.add(calc.getNetPay());
                created++;
            }

            session.getMapper(PayrollBatchMapper.class).updateTotals(batch.getBatchId(), totalNet);

            RunResult result = new RunResult();
            result.setBatchId(batch.getBatchId());
            result.setCreatedCount(created);
            result.setSkipped(skipped);
            result.setTotalNet(totalNet);
            return result;
        });
    }

    public List<PayrollBatch> listBatches(UserSession user) {
        assertUser(user);
        List<PayrollBatch> list = SqlExecutor.execute(PayrollBatchMapper.class, PayrollBatchMapper::selectAll);
        return list == null ? List.of() : list;
    }

    public PayrollBatch getBatch(UserSession user, Integer batchId) {
        assertUser(user);
        PayrollBatch b = SqlExecutor.execute(PayrollBatchMapper.class, m -> m.selectByPrimaryKey(batchId));
        if (b == null) {
            throw new ValidationException("Không tìm thấy batch");
        }
        return b;
    }

    public List<Payslip> listPayslips(UserSession user, Integer batchId) {
        assertUser(user);
        List<Payslip> list = SqlExecutor.execute(PayslipMapper.class, m -> m.selectByBatch(batchId));
        return list == null ? List.of() : list;
    }

    public Payslip getPayslip(UserSession user, Integer payslipId) {
        assertUser(user);
        Payslip p = SqlExecutor.execute(PayslipMapper.class, m -> m.selectByPrimaryKey(payslipId));
        if (p == null) {
            throw new ValidationException("Không tìm thấy phiếu lương");
        }
        return p;
    }

    public List<PayslipDetail> getDetails(UserSession user, Integer payslipId) {
        assertUser(user);
        List<PayslipDetail> list = SqlExecutor.execute(PayslipDetailMapper.class,
                m -> m.selectByPayslipId(payslipId));
        return list == null ? List.of() : list;
    }

    private void assertUser(UserSession user) {
        if (user == null || !user.isUser()) {
            throw new UnauthorizedException("Không có quyền");
        }
    }

    public static final class RunResult {
        private Integer batchId;
        private int createdCount;
        private List<String> skipped = List.of();
        private BigDecimal totalNet = BigDecimal.ZERO;

        public Integer getBatchId() { return batchId; }
        public void setBatchId(Integer batchId) { this.batchId = batchId; }
        public int getCreatedCount() { return createdCount; }
        public void setCreatedCount(int createdCount) { this.createdCount = createdCount; }
        public List<String> getSkipped() { return skipped; }
        public void setSkipped(List<String> skipped) {
            this.skipped = skipped == null ? List.of() : skipped;
        }
        public BigDecimal getTotalNet() { return totalNet; }
        public void setTotalNet(BigDecimal totalNet) { this.totalNet = totalNet; }
    }
}
