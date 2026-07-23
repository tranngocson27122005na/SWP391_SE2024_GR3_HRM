package com.hrm.persistence.entity;

import java.math.BigDecimal;

public class PayslipDetail {
    private Integer detailId;
    private Integer payslipId;
    private Integer elementId;
    private BigDecimal amount;
    private String remark;
    private String elementCode;
    private String elementName;

    public Integer getDetailId() { return detailId; }
    public void setDetailId(Integer detailId) { this.detailId = detailId; }
    public Integer getPayslipId() { return payslipId; }
    public void setPayslipId(Integer payslipId) { this.payslipId = payslipId; }
    public Integer getElementId() { return elementId; }
    public void setElementId(Integer elementId) { this.elementId = elementId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getElementCode() { return elementCode; }
    public void setElementCode(String elementCode) { this.elementCode = elementCode; }
    public String getElementName() { return elementName; }
    public void setElementName(String elementName) { this.elementName = elementName; }
}
