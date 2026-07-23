package com.hrm.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;

public class LeaveBalance {
    private Integer balanceId;

    private Integer employeeId;

    private Integer year;

    private BigDecimal entitledDays;

    private BigDecimal takenDays;

    private BigDecimal remainingDays;

    private Byte status;

    private Date createdAt;

    public Integer getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(Integer balanceId) {
        this.balanceId = balanceId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BigDecimal getEntitledDays() {
        return entitledDays;
    }

    public void setEntitledDays(BigDecimal entitledDays) {
        this.entitledDays = entitledDays;
    }

    public BigDecimal getTakenDays() {
        return takenDays;
    }

    public void setTakenDays(BigDecimal takenDays) {
        this.takenDays = takenDays;
    }

    public BigDecimal getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(BigDecimal remainingDays) {
        this.remainingDays = remainingDays;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}