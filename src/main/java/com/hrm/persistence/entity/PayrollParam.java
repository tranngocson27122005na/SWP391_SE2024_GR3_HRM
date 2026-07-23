package com.hrm.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;

public class PayrollParam {
    private Integer paramId;
    private String paramCode;
    private BigDecimal paramValue;
    private String note;
    private Byte status;
    private Date updatedAt;

    public Integer getParamId() { return paramId; }
    public void setParamId(Integer paramId) { this.paramId = paramId; }
    public String getParamCode() { return paramCode; }
    public void setParamCode(String paramCode) { this.paramCode = paramCode; }
    public BigDecimal getParamValue() { return paramValue; }
    public void setParamValue(BigDecimal paramValue) { this.paramValue = paramValue; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public Byte getStatus() { return status; }
    public void setStatus(Byte status) { this.status = status; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
