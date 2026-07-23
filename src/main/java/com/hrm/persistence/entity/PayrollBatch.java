package com.hrm.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;

public class PayrollBatch {
    private Integer batchId;
    private String batchName;
    private Integer periodMonth;
    private Integer periodYear;
    private Integer importId;
    private Date runAt;
    private Integer runBy;
    private Byte batchStatus;
    private BigDecimal totalNet;
    private Byte status;
    private Date createdAt;

    public Integer getBatchId() { return batchId; }
    public void setBatchId(Integer batchId) { this.batchId = batchId; }
    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = batchName; }
    public Integer getPeriodMonth() { return periodMonth; }
    public void setPeriodMonth(Integer periodMonth) { this.periodMonth = periodMonth; }
    public Integer getPeriodYear() { return periodYear; }
    public void setPeriodYear(Integer periodYear) { this.periodYear = periodYear; }
    public Integer getImportId() { return importId; }
    public void setImportId(Integer importId) { this.importId = importId; }
    public Date getRunAt() { return runAt; }
    public void setRunAt(Date runAt) { this.runAt = runAt; }
    public Integer getRunBy() { return runBy; }
    public void setRunBy(Integer runBy) { this.runBy = runBy; }
    public Byte getBatchStatus() { return batchStatus; }
    public void setBatchStatus(Byte batchStatus) { this.batchStatus = batchStatus; }
    public BigDecimal getTotalNet() { return totalNet; }
    public void setTotalNet(BigDecimal totalNet) { this.totalNet = totalNet; }
    public Byte getStatus() { return status; }
    public void setStatus(Byte status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
