package com.hrm.persistence.entity;

public class PayrollElement {
    private Integer elementId;
    private String elementCode;
    private String elementName;
    private Byte category;
    private Byte isTaxable;
    private Byte isInsurable;
    private Integer calcOrder;
    private Byte status;

    public Integer getElementId() { return elementId; }
    public void setElementId(Integer elementId) { this.elementId = elementId; }
    public String getElementCode() { return elementCode; }
    public void setElementCode(String elementCode) { this.elementCode = elementCode; }
    public String getElementName() { return elementName; }
    public void setElementName(String elementName) { this.elementName = elementName; }
    public Byte getCategory() { return category; }
    public void setCategory(Byte category) { this.category = category; }
    public Byte getIsTaxable() { return isTaxable; }
    public void setIsTaxable(Byte isTaxable) { this.isTaxable = isTaxable; }
    public Byte getIsInsurable() { return isInsurable; }
    public void setIsInsurable(Byte isInsurable) { this.isInsurable = isInsurable; }
    public Integer getCalcOrder() { return calcOrder; }
    public void setCalcOrder(Integer calcOrder) { this.calcOrder = calcOrder; }
    public Byte getStatus() { return status; }
    public void setStatus(Byte status) { this.status = status; }
}
