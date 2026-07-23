package com.hrm.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;

public class AttendanceSummary {
    private Integer summaryId;
    private Integer employeeId;
    private Integer importId;
    private Integer periodMonth;
    private Integer periodYear;
    private Date summaryPeriod;
    private BigDecimal totalWorkDays;
    private BigDecimal actualWorkDays;
    private BigDecimal paidLeaveDays;
    private BigDecimal unpaidLeaveDays;
    private BigDecimal holidayDays;
    private BigDecimal otWeekdayHours;
    private BigDecimal otWeekendHours;
    private BigDecimal otHolidayHours;
    private Integer lateEarlyBlocks;
    private BigDecimal totalOtHours;
    private Integer totalLateCount;
    private Integer totalEarlyCount;
    private BigDecimal totalAbsentDays;
    private BigDecimal totalLeaveDays;
    private Byte summaryStatus;
    private Byte status;
    private Date createdAt;
    private String employeeCode;
    private String fullName;

    public Integer getSummaryId() { return summaryId; }
    public void setSummaryId(Integer summaryId) { this.summaryId = summaryId; }
    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }
    public Integer getImportId() { return importId; }
    public void setImportId(Integer importId) { this.importId = importId; }
    public Integer getPeriodMonth() { return periodMonth; }
    public void setPeriodMonth(Integer periodMonth) { this.periodMonth = periodMonth; }
    public Integer getPeriodYear() { return periodYear; }
    public void setPeriodYear(Integer periodYear) { this.periodYear = periodYear; }
    public Date getSummaryPeriod() { return summaryPeriod; }
    public void setSummaryPeriod(Date summaryPeriod) { this.summaryPeriod = summaryPeriod; }
    public BigDecimal getTotalWorkDays() { return totalWorkDays; }
    public void setTotalWorkDays(BigDecimal totalWorkDays) { this.totalWorkDays = totalWorkDays; }
    public BigDecimal getActualWorkDays() { return actualWorkDays; }
    public void setActualWorkDays(BigDecimal actualWorkDays) { this.actualWorkDays = actualWorkDays; }
    public BigDecimal getPaidLeaveDays() { return paidLeaveDays; }
    public void setPaidLeaveDays(BigDecimal paidLeaveDays) { this.paidLeaveDays = paidLeaveDays; }
    public BigDecimal getUnpaidLeaveDays() { return unpaidLeaveDays; }
    public void setUnpaidLeaveDays(BigDecimal unpaidLeaveDays) { this.unpaidLeaveDays = unpaidLeaveDays; }
    public BigDecimal getHolidayDays() { return holidayDays; }
    public void setHolidayDays(BigDecimal holidayDays) { this.holidayDays = holidayDays; }
    public BigDecimal getOtWeekdayHours() { return otWeekdayHours; }
    public void setOtWeekdayHours(BigDecimal otWeekdayHours) { this.otWeekdayHours = otWeekdayHours; }
    public BigDecimal getOtWeekendHours() { return otWeekendHours; }
    public void setOtWeekendHours(BigDecimal otWeekendHours) { this.otWeekendHours = otWeekendHours; }
    public BigDecimal getOtHolidayHours() { return otHolidayHours; }
    public void setOtHolidayHours(BigDecimal otHolidayHours) { this.otHolidayHours = otHolidayHours; }
    public Integer getLateEarlyBlocks() { return lateEarlyBlocks; }
    public void setLateEarlyBlocks(Integer lateEarlyBlocks) { this.lateEarlyBlocks = lateEarlyBlocks; }
    public BigDecimal getTotalOtHours() { return totalOtHours; }
    public void setTotalOtHours(BigDecimal totalOtHours) { this.totalOtHours = totalOtHours; }
    public Integer getTotalLateCount() { return totalLateCount; }
    public void setTotalLateCount(Integer totalLateCount) { this.totalLateCount = totalLateCount; }
    public Integer getTotalEarlyCount() { return totalEarlyCount; }
    public void setTotalEarlyCount(Integer totalEarlyCount) { this.totalEarlyCount = totalEarlyCount; }
    public BigDecimal getTotalAbsentDays() { return totalAbsentDays; }
    public void setTotalAbsentDays(BigDecimal totalAbsentDays) { this.totalAbsentDays = totalAbsentDays; }
    public BigDecimal getTotalLeaveDays() { return totalLeaveDays; }
    public void setTotalLeaveDays(BigDecimal totalLeaveDays) { this.totalLeaveDays = totalLeaveDays; }
    public Byte getSummaryStatus() { return summaryStatus; }
    public void setSummaryStatus(Byte summaryStatus) { this.summaryStatus = summaryStatus; }
    public Byte getStatus() { return status; }
    public void setStatus(Byte status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}
