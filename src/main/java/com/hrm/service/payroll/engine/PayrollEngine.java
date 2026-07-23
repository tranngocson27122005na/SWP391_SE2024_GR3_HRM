package com.hrm.service.payroll.engine;

import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.persistence.entity.AttendanceSummary;
import com.hrm.persistence.entity.Contract;
import com.hrm.persistence.entity.Employee;
import com.hrm.persistence.entity.enums.ContractType;
import com.hrm.persistence.entity.enums.EmploymentGroup;
import com.hrm.persistence.entity.enums.SalaryType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Layer 3 v2 — atomic + aggregation (tham số hóa).
 */
public class PayrollEngine {

    private static final int SCALE = 2;
    private static final RoundingMode RM = RoundingMode.HALF_UP;

    public EmpCalcResult calculate(Employee emp, Contract contract, AttendanceSummary summary,
                                   Map<String, BigDecimal> params, int dependentCount,
                                   int periodMonth, int periodYear) {
        require(params, "PROBATION_RATE");
        require(params, "SOCIAL_INS_RATE");
        require(params, "MEAL_ALLOWANCE_THRESHOLD");
        require(params, "OT_SALARY_WF");
        require(params, "HOURS_PER_WORK_DAY");
        require(params, "DEDUCTION_RATE_PER_BLOCK");
        require(params, "OT_RATE_WEEKEND");
        require(params, "OT_RATE_HOLIDAY");
        require(params, "PIT_PERSONAL_EXEMPTION");
        require(params, "PIT_DEPENDENT_EXEMPTION");

        List<PitBracket> brackets = buildPitBrackets(params);
        if (brackets.isEmpty()) {
            throw new ValidationException("Thiếu PIT brackets trong payroll_param");
        }

        EmploymentGroup group = EmploymentGroup.fromCode(
                emp.getEmploymentGroup() == null ? null : emp.getEmploymentGroup().intValue());
        if (group == null) {
            group = EmploymentGroup.OFFICE;
        }
        SalaryType salaryType = SalaryType.fromCode(
                contract.getSalaryType() == null ? null : contract.getSalaryType().intValue());
        if (salaryType == null) {
            salaryType = SalaryType.MONTHLY;
        }
        ContractType contractType = ContractType.fromCode(
                contract.getContractType() == null ? null : contract.getContractType().intValue());
        if (contract.getBasicSalary() == null) {
            throw new ValidationException("HĐ thiếu basic_salary: " + emp.getEmployeeCode());
        }
        BigDecimal contractSalary = nz(contract.getBasicSalary());
        BigDecimal hoursPerDay = p(params, "HOURS_PER_WORK_DAY");
        BigDecimal actualWorkDays = nz(summary.getActualWorkDays());
        BigDecimal totalWorkDays = nz(summary.getTotalWorkDays());
        if (totalWorkDays.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("TotalWorkDays phải > 0 cho NV " + emp.getEmployeeCode());
        }

        // PAY-RULE-BASE-001
        BigDecimal calculatedBase;
        if (group == EmploymentGroup.OFFICE) {
            calculatedBase = contractSalary;
        } else {
            // FACTORY
            if (salaryType == SalaryType.HOURLY) {
                calculatedBase = contractSalary.multiply(actualWorkDays).multiply(hoursPerDay);
            } else {
                // MONTHLY: quy đổi theo tỷ lệ ngày làm thực tế
                calculatedBase = contractSalary.multiply(actualWorkDays)
                        .divide(totalWorkDays, 8, RM);
            }
        }
        calculatedBase = money(calculatedBase);

        // PAY-RULE-PROB-001
        boolean probation = contractType == ContractType.PROBATION;
        BigDecimal probationRate = p(params, "PROBATION_RATE");
        BigDecimal finalBase = probation
                ? money(calculatedBase.multiply(probationRate))
                : calculatedBase;

        // PAY-RULE-HOURLY-001 (sau PROB — dùng Final_Base cho MONTHLY)
        BigDecimal hourly;
        if (salaryType == SalaryType.HOURLY) {
            hourly = probation ? money(contractSalary.multiply(probationRate)) : contractSalary;
        } else {
            hourly = finalBase.divide(totalWorkDays, 8, RM).divide(hoursPerDay, 8, RM);
        }

        // PAY-RULE-SENIOR-001
        Date periodEnd = periodEndDate(periodYear, periodMonth);
        BigDecimal seniority = money(seniorityAllowance(emp.getJoiningDate(), periodEnd, finalBase));

        // PAY-RULE-MEAL-000/001/002
        BigDecimal otWeekday = nz(summary.getOtWeekdayHours());
        BigDecimal totalMeal = BigDecimal.ZERO;
        if (group == EmploymentGroup.FACTORY) {
            totalMeal = money(otWeekday.multiply(p(params, "OT_SALARY_WF")));
        }
        BigDecimal mealThreshold = p(params, "MEAL_ALLOWANCE_THRESHOLD");
        BigDecimal mealFixed = money(totalMeal.min(mealThreshold));
        BigDecimal mealSurp = money(totalMeal.subtract(mealFixed).max(BigDecimal.ZERO));

        // PAY-RULE-OT-WE-HOL-001
        BigDecimal otWe = money(nz(summary.getOtWeekendHours())
                .multiply(hourly).multiply(p(params, "OT_RATE_WEEKEND")));
        BigDecimal otHol = money(nz(summary.getOtHolidayHours())
                .multiply(hourly).multiply(p(params, "OT_RATE_HOLIDAY")));
        BigDecimal totalOt = money(otWe.add(otHol));

        // PAY-RULE-TIME-DEDUCT-001
        int blocks = summary.getLateEarlyBlocks() == null ? 0 : summary.getLateEarlyBlocks();
        BigDecimal timeDeduct = money(hourly.multiply(BigDecimal.valueOf(blocks))
                .multiply(p(params, "DEDUCTION_RATE_PER_BLOCK")));

        // PAY-RULE-INS-001
        BigDecimal insBase = finalBase.add(seniority);
        BigDecimal bhEmp = money(insBase.multiply(p(params, "SOCIAL_INS_RATE")));

        // Aggregations
        BigDecimal earnings = money(finalBase.add(totalOt).add(seniority).add(totalMeal));
        BigDecimal taxableGross = money(finalBase.add(totalOt).add(seniority).add(mealSurp)
                .subtract(timeDeduct));
        BigDecimal personalExempt = p(params, "PIT_PERSONAL_EXEMPTION");
        BigDecimal dependentExempt = p(params, "PIT_DEPENDENT_EXEMPTION")
                .multiply(BigDecimal.valueOf(Math.max(0, dependentCount)));
        BigDecimal netTaxable = taxableGross.subtract(bhEmp).subtract(personalExempt)
                .subtract(dependentExempt).max(BigDecimal.ZERO);
        BigDecimal pit = money(progressiveTax(netTaxable, brackets));

        BigDecimal internalDeduct = timeDeduct;
        BigDecimal statutory = money(bhEmp.add(pit));
        BigDecimal netPay = money(earnings.subtract(internalDeduct).subtract(statutory));

        EmpCalcResult r = new EmpCalcResult();
        r.setFinalBase(finalBase);
        r.setSeniority(seniority);
        r.setMealFixed(mealFixed);
        r.setMealSurp(mealSurp);
        r.setOtWeekend(otWe);
        r.setOtHoliday(otHol);
        r.setTimeDeduct(timeDeduct);
        r.setBhEmp(bhEmp);
        r.setPit(pit);
        r.setNetPay(netPay);
        r.setHourly(hourly);
        r.setDependentCount(dependentCount);
        return r;
    }

    public static List<PitBracket> buildPitBrackets(Map<String, BigDecimal> params) {
        List<PitBracket> list = new ArrayList<>();
        BigDecimal prevUpper = BigDecimal.ZERO;
        for (int level = 1; level <= 6; level++) {
            BigDecimal upper = params.get("PIT_BRACKET_" + level + "_UPPER");
            BigDecimal rate = params.get("PIT_BRACKET_" + level + "_RATE");
            if (upper == null || rate == null) {
                throw new ValidationException("Thiếu PIT_BRACKET_" + level + "_UPPER/RATE");
            }
            list.add(new PitBracket(level, prevUpper, upper, rate));
            prevUpper = upper;
        }
        BigDecimal rate7 = params.get("PIT_BRACKET_7_RATE");
        if (rate7 == null) {
            throw new ValidationException("Thiếu PIT_BRACKET_7_RATE");
        }
        list.add(new PitBracket(7, prevUpper, null, rate7));
        return list;
    }

    static BigDecimal progressiveTax(BigDecimal income, List<PitBracket> brackets) {
        if (income == null || income.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal tax = BigDecimal.ZERO;
        for (PitBracket b : brackets) {
            if (income.compareTo(b.lowerBound) <= 0) {
                break;
            }
            BigDecimal upper = b.upperBound == null ? income : income.min(b.upperBound);
            BigDecimal slice = upper.subtract(b.lowerBound);
            if (slice.compareTo(BigDecimal.ZERO) > 0) {
                tax = tax.add(slice.multiply(b.rate));
            }
            if (b.upperBound == null || income.compareTo(b.upperBound) <= 0) {
                break;
            }
        }
        return tax;
    }

    static BigDecimal seniorityAllowance(Date joiningDate, Date periodEnd, BigDecimal finalBase) {
        if (joiningDate == null || periodEnd == null || finalBase == null) {
            return BigDecimal.ZERO;
        }
        LocalDate join = toLocalDate(joiningDate);
        LocalDate end = toLocalDate(periodEnd);
        if (end.isBefore(join)) {
            return BigDecimal.ZERO;
        }
        int years = Period.between(join, end).getYears();
        if (years < 5) {
            return BigDecimal.ZERO;
        }
        // ≥5 → min(5% + (years-5)×1%, 100%)
        BigDecimal rate = new BigDecimal("0.05")
                .add(BigDecimal.valueOf(years - 5).multiply(new BigDecimal("0.01")));
        if (rate.compareTo(BigDecimal.ONE) > 0) {
            rate = BigDecimal.ONE;
        }
        return finalBase.multiply(rate);
    }

    static Date periodEndDate(int year, int month) {
        Calendar cal = new GregorianCalendar(year, month - 1, 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    private static LocalDate toLocalDate(Date date) {
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static void require(Map<String, BigDecimal> params, String code) {
        if (params == null || params.get(code) == null) {
            throw new ValidationException("Thiếu tham số bắt buộc: " + code);
        }
    }

    private static BigDecimal p(Map<String, BigDecimal> params, String code) {
        return params.get(code);
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private static BigDecimal money(BigDecimal v) {
        return nz(v).setScale(SCALE, RM);
    }

    public static Map<String, BigDecimal> toParamMap(List<com.hrm.persistence.entity.PayrollParam> rows) {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        if (rows != null) {
            for (com.hrm.persistence.entity.PayrollParam row : rows) {
                if (row.getParamCode() != null && row.getParamValue() != null) {
                    map.put(row.getParamCode(), row.getParamValue());
                }
            }
        }
        return map;
    }

    public static final class PitBracket {
        private final int level;
        private final BigDecimal lowerBound;
        private final BigDecimal upperBound;
        private final BigDecimal rate;

        public PitBracket(int level, BigDecimal lowerBound, BigDecimal upperBound, BigDecimal rate) {
            this.level = level;
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.rate = rate;
        }

        public int getLevel() { return level; }
        public BigDecimal getLowerBound() { return lowerBound; }
        public BigDecimal getUpperBound() { return upperBound; }
        public BigDecimal getRate() { return rate; }
    }

    public static final class EmpCalcResult {
        private BigDecimal finalBase = BigDecimal.ZERO;
        private BigDecimal seniority = BigDecimal.ZERO;
        private BigDecimal mealFixed = BigDecimal.ZERO;
        private BigDecimal mealSurp = BigDecimal.ZERO;
        private BigDecimal otWeekend = BigDecimal.ZERO;
        private BigDecimal otHoliday = BigDecimal.ZERO;
        private BigDecimal timeDeduct = BigDecimal.ZERO;
        private BigDecimal bhEmp = BigDecimal.ZERO;
        private BigDecimal pit = BigDecimal.ZERO;
        private BigDecimal netPay = BigDecimal.ZERO;
        private BigDecimal hourly = BigDecimal.ZERO;
        private int dependentCount;

        public Map<String, BigDecimal> elementAmounts() {
            Map<String, BigDecimal> m = new LinkedHashMap<>();
            m.put("BASE_SALARY", finalBase);
            m.put("SENIORITY_ALLOW", seniority);
            m.put("MEAL_ALLOW_FIXED", mealFixed);
            m.put("MEAL_ALLOW_SURP", mealSurp);
            m.put("OT_SALARY_WE", otWeekend);
            m.put("OT_SALARY_HOL", otHoliday);
            m.put("TIME_DEDUCT", timeDeduct);
            m.put("BH_EMP", bhEmp);
            m.put("PIT_TAX", pit);
            return m;
        }

        public BigDecimal getFinalBase() { return finalBase; }
        public void setFinalBase(BigDecimal finalBase) { this.finalBase = finalBase; }
        public BigDecimal getSeniority() { return seniority; }
        public void setSeniority(BigDecimal seniority) { this.seniority = seniority; }
        public BigDecimal getMealFixed() { return mealFixed; }
        public void setMealFixed(BigDecimal mealFixed) { this.mealFixed = mealFixed; }
        public BigDecimal getMealSurp() { return mealSurp; }
        public void setMealSurp(BigDecimal mealSurp) { this.mealSurp = mealSurp; }
        public BigDecimal getOtWeekend() { return otWeekend; }
        public void setOtWeekend(BigDecimal otWeekend) { this.otWeekend = otWeekend; }
        public BigDecimal getOtHoliday() { return otHoliday; }
        public void setOtHoliday(BigDecimal otHoliday) { this.otHoliday = otHoliday; }
        public BigDecimal getTimeDeduct() { return timeDeduct; }
        public void setTimeDeduct(BigDecimal timeDeduct) { this.timeDeduct = timeDeduct; }
        public BigDecimal getBhEmp() { return bhEmp; }
        public void setBhEmp(BigDecimal bhEmp) { this.bhEmp = bhEmp; }
        public BigDecimal getPit() { return pit; }
        public void setPit(BigDecimal pit) { this.pit = pit; }
        public BigDecimal getNetPay() { return netPay; }
        public void setNetPay(BigDecimal netPay) { this.netPay = netPay; }
        public BigDecimal getHourly() { return hourly; }
        public void setHourly(BigDecimal hourly) { this.hourly = hourly; }
        public int getDependentCount() { return dependentCount; }
        public void setDependentCount(int dependentCount) { this.dependentCount = dependentCount; }
    }
}
