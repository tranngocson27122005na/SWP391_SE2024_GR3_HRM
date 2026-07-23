package com.hrm.persistence.entity;

import java.math.BigDecimal;

public class PayrollBatchParam {
    private Integer id;
    private Integer batchId;
    private String paramCode;
    private BigDecimal paramValue;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getBatchId() { return batchId; }
    public void setBatchId(Integer batchId) { this.batchId = batchId; }
    public String getParamCode() { return paramCode; }
    public void setParamCode(String paramCode) { this.paramCode = paramCode; }
    public BigDecimal getParamValue() { return paramValue; }
    public void setParamValue(BigDecimal paramValue) { this.paramValue = paramValue; }
}
