package com.hrm.service.attendance;

import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.BusinessException;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.persistence.executor.SqlExecutor;
import com.hrm.persistence.entity.AttendanceImport;
import com.hrm.persistence.entity.AttendanceSummary;
import com.hrm.persistence.entity.Employee;
import com.hrm.persistence.entity.enums.ActiveStatus;
import com.hrm.persistence.entity.enums.EmploymentGroup;
import com.hrm.persistence.mapper.AttendanceImportMapper;
import com.hrm.persistence.mapper.AttendanceSummaryMapper;
import com.hrm.persistence.mapper.EmployeeMapper;
import com.hrm.service.payroll.PayrollCleanupService;
import org.apache.ibatis.session.SqlSession;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceImportService {

    private final PayrollCleanupService payrollCleanup = new PayrollCleanupService();

    public ImportResult importCsv(UserSession user, InputStream csvStream, String fileName,
                                  int periodMonth, int periodYear) {
        assertHr(user);
        if (periodMonth < 1 || periodMonth > 12 || periodYear < 2000) {
            throw new ValidationException("Kỳ lương không hợp lệ");
        }
        if (csvStream == null) {
            throw new ValidationException("Chọn file CSV");
        }

        List<String> errors = new ArrayList<>();
        List<AttendanceSummary> rows = parseCsv(csvStream, periodMonth, periodYear, errors);

        return SqlExecutor.executeTransaction(session -> {
            payrollCleanup.deleteAllPayrollResults(session);

            AttendanceImport imp = new AttendanceImport();
            imp.setFileName(fileName == null ? "upload.csv" : fileName);
            imp.setImportedBy(user.getUserId() == null ? null : user.getUserId().intValue());
            imp.setTotalRecord(0);
            imp.setStatus((byte) 1);
            session.getMapper(AttendanceImportMapper.class).insert(imp);

            int ok = 0;
            AttendanceSummaryMapper sumMapper = session.getMapper(AttendanceSummaryMapper.class);
            EmployeeMapper empMapper = session.getMapper(EmployeeMapper.class);

            for (AttendanceSummary row : rows) {
                Employee emp = empMapper.selectByCode(row.getEmployeeCode());
                if (emp == null) {
                    errors.add("Không tìm thấy NV: " + row.getEmployeeCode());
                    continue;
                }
                row.setEmployeeId(emp.getEmployeeId());
                row.setImportId(imp.getImportId());
                if (EmploymentGroup.OFFICE.equals(EmploymentGroup.fromCode(
                        emp.getEmploymentGroup() == null ? null : emp.getEmploymentGroup().intValue()))) {
                    row.setUnpaidLeaveDays(BigDecimal.ZERO);
                }
                AttendanceSummary existing = sumMapper.selectByEmpPeriod(
                        emp.getEmployeeId(), periodYear, periodMonth);
                if (existing == null) {
                    sumMapper.insert(row);
                } else {
                    row.setSummaryId(existing.getSummaryId());
                    sumMapper.updateByPrimaryKey(row);
                }
                ok++;
            }

            imp.setTotalRecord(ok);
            session.getMapper(AttendanceImportMapper.class).updateByPrimaryKey(imp);

            ImportResult result = new ImportResult();
            result.setImportId(imp.getImportId());
            result.setSuccessCount(ok);
            result.setErrors(errors);
            return result;
        });
    }

    public List<AttendanceSummary> listByPeriod(UserSession user, int periodMonth, int periodYear) {
        assertHr(user);
        List<AttendanceSummary> list = SqlExecutor.execute(AttendanceSummaryMapper.class,
                m -> m.selectByPeriod(periodYear, periodMonth));
        return list == null ? List.of() : list;
    }

    private List<AttendanceSummary> parseCsv(InputStream in, int periodMonth, int periodYear,
                                             List<String> errors) {
        List<AttendanceSummary> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                throw new ValidationException("File CSV trống");
            }
            headerLine = stripBom(headerLine);
            Map<String, Integer> idx = indexHeader(headerLine);
            require(idx, "EmployeeCode");
            require(idx, "TotalWorkDays");
            require(idx, "ActualWorkDays");
            require(idx, "Late_Early_Blocks");

            String line;
            int lineNo = 1;
            while ((line = br.readLine()) != null) {
                lineNo++;
                if (line.isBlank()) {
                    continue;
                }
                String[] cols = line.split(",", -1);
                try {
                    AttendanceSummary s = new AttendanceSummary();
                    s.setEmployeeCode(cell(cols, idx, "EmployeeCode").trim());
                    s.setTotalWorkDays(dec(cols, idx, "TotalWorkDays"));
                    s.setActualWorkDays(dec(cols, idx, "ActualWorkDays"));
                    s.setPaidLeaveDays(decOrZero(cols, idx, "TotalPaidLeave"));
                    s.setUnpaidLeaveDays(decOrZero(cols, idx, "TotalUnpaidLeave"));
                    s.setHolidayDays(decOrZero(cols, idx, "TotalHoliday"));
                    s.setOtWeekdayHours(decOrZero(cols, idx, "OT_Weekday_Hours"));
                    s.setOtWeekendHours(decOrZero(cols, idx, "OT_Weekend_Hours"));
                    s.setOtHolidayHours(decOrZero(cols, idx, "OT_Holiday_Hours"));
                    s.setLateEarlyBlocks(intOrZero(cols, idx, "Late_Early_Blocks"));
                    BigDecimal otSum = nz(s.getOtWeekdayHours()).add(nz(s.getOtWeekendHours())).add(nz(s.getOtHolidayHours()));
                    s.setTotalOtHours(otSum);
                    s.setPeriodMonth(periodMonth);
                    s.setPeriodYear(periodYear);
                    Calendar cal = new GregorianCalendar(periodYear, periodMonth - 1, 1);
                    s.setSummaryPeriod(cal.getTime());
                    s.setSummaryStatus((byte) 1);
                    s.setStatus(ActiveStatus.ACTIVE.toByte());
                    s.setTotalLateCount(0);
                    s.setTotalEarlyCount(0);
                    s.setTotalAbsentDays(BigDecimal.ZERO);
                    s.setTotalLeaveDays(nz(s.getPaidLeaveDays()).add(nz(s.getUnpaidLeaveDays())));
                    out.add(s);
                } catch (Exception e) {
                    errors.add("Dòng " + lineNo + ": " + e.getMessage());
                }
            }
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Không đọc được CSV: " + e.getMessage());
        }
        if (out.isEmpty() && errors.isEmpty()) {
            throw new ValidationException("CSV không có dòng dữ liệu");
        }
        return out;
    }

    private static String stripBom(String s) {
        if (s != null && !s.isEmpty() && s.charAt(0) == '\uFEFF') {
            return s.substring(1);
        }
        return s;
    }

    private static Map<String, Integer> indexHeader(String header) {
        String[] parts = header.split(",", -1);
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < parts.length; i++) {
            map.put(parts[i].trim(), i);
        }
        return map;
    }

    private static void require(Map<String, Integer> idx, String col) {
        if (!idx.containsKey(col)) {
            throw new ValidationException("Thiếu cột CSV: " + col);
        }
    }

    private static String cell(String[] cols, Map<String, Integer> idx, String col) {
        Integer i = idx.get(col);
        if (i == null || i >= cols.length) {
            return "";
        }
        return cols[i] == null ? "" : cols[i];
    }

    private static BigDecimal dec(String[] cols, Map<String, Integer> idx, String col) {
        String v = cell(cols, idx, col).trim();
        if (v.isEmpty()) {
            throw new ValidationException("Thiếu giá trị " + col);
        }
        return new BigDecimal(v);
    }

    private static BigDecimal decOrZero(String[] cols, Map<String, Integer> idx, String col) {
        if (!idx.containsKey(col)) {
            return BigDecimal.ZERO;
        }
        String v = cell(cols, idx, col).trim();
        if (v.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(v);
    }

    private static int intOrZero(String[] cols, Map<String, Integer> idx, String col) {
        String v = cell(cols, idx, col).trim();
        if (v.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(v);
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private void assertHr(UserSession user) {
        if (user == null || !user.isUser()) {
            throw new UnauthorizedException("Không có quyền");
        }
    }

    public static final class ImportResult {
        private Integer importId;
        private int successCount;
        private List<String> errors = List.of();

        public Integer getImportId() { return importId; }
        public void setImportId(Integer importId) { this.importId = importId; }
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors == null ? List.of() : errors; }
    }
}
