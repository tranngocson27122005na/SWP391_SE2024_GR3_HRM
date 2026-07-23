package com.hrm.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;

public class Payslip {
    private Integer payslipId;
    private Integer batchId;
    private Integer employeeId;
    private Integer contractId;
    private Byte payslipStatus;
    private BigDecimal netPay;
    private Byte status;
    private Date createdAt;
    private String employeeCode;
    private String fullName;

    public Integer getPayslipId() { return payslipId; }
    public void setPayslipId(Integer payslipId) { this.payslipId = payslipId; }
    public Integer getBatchId() { return batchId; }
    public void setBatchId(Integer batchId) { this.batchId = batchId; }
    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }
    public Integer getContractId() { return contractId; }
    public void setContractId(Integer contractId) { this.contractId = contractId; }
    public Byte getPayslipStatus() { return payslipStatus; }
    public void setPayslipStatus(Byte payslipStatus) { this.payslipStatus = payslipStatus; }
    public BigDecimal getNetPay() { return netPay; }
    public void setNetPay(BigDecimal netPay) { this.netPay = netPay; }
    public Byte getStatus() { return status; }
    public void setStatus(Byte status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}
