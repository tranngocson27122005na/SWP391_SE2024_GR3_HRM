package com.hrm.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;

public class DailyAttendance {
    private Integer attendanceId;

    private Integer employeeId;

    private Integer importId;

    private Date attendanceDate;

    private Date checkInTime;

    private Date checkOutTime;

    private BigDecimal workHours;

    private BigDecimal otHours;

    private Integer lateEarlyBlocks;

    private Byte attendanceStatus;

    private Byte status;

    private Date createdAt;

    public Integer getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Integer attendanceId) {
        this.attendanceId = attendanceId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getImportId() {
        return importId;
    }

    public void setImportId(Integer importId) {
        this.importId = importId;
    }

    public Date getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(Date attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public Date getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Date checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Date getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(Date checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public BigDecimal getWorkHours() {
        return workHours;
    }

    public void setWorkHours(BigDecimal workHours) {
        this.workHours = workHours;
    }

    public BigDecimal getOtHours() {
        return otHours;
    }

    public void setOtHours(BigDecimal otHours) {
        this.otHours = otHours;
    }

    public Integer getLateEarlyBlocks() {
        return lateEarlyBlocks;
    }

    public void setLateEarlyBlocks(Integer lateEarlyBlocks) {
        this.lateEarlyBlocks = lateEarlyBlocks;
    }

    public Byte getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(Byte attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
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