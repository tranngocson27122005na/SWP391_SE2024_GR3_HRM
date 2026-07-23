package com.hrm.dto.response;

import java.util.Date;

public class DependentResponse {
    private Integer dependentId;
    private Integer employeeId;
    private String fullName;
    private String relationship;
    private String taxCode;
    private Date startDate;
    private Date endDate;
    private Integer status;
    private Date createdAt;

    public Integer getDependentId() { return dependentId; }
    public void setDependentId(Integer dependentId) { this.dependentId = dependentId; }
    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
