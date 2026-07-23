package com.hrm.persistence.entity;

import java.util.Date;

public class AttendanceImport {
    private Integer importId;

    private String fileName;

    private Date importDate;

    private Integer importedBy;

    private Byte status;

    private Integer totalRecord;

    public Integer getImportId() {
        return importId;
    }

    public void setImportId(Integer importId) {
        this.importId = importId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getImportDate() {
        return importDate;
    }

    public void setImportDate(Date importDate) {
        this.importDate = importDate;
    }

    public Integer getImportedBy() {
        return importedBy;
    }

    public void setImportedBy(Integer importedBy) {
        this.importedBy = importedBy;
    }

    public Integer getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(Integer totalRecord) {
        this.totalRecord = totalRecord;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
}