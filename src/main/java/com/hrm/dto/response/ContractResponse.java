package com.hrm.dto.response;

import java.math.BigDecimal;
import java.util.Date;

public class ContractResponse {
    private Integer contractId;
    private Integer employeeId;
    private Integer contractType;
    private String contractTypeLabel;
    private Date startDate;
    private Date endDate;
    private BigDecimal basicSalary;
    private Integer salaryType;
    private String salaryTypeLabel;
    private Integer status;
    private Date createdAt;
    private boolean current;

    public Integer getContractId() { return contractId; }
    public void setContractId(Integer contractId) { this.contractId = contractId; }
    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }
    public Integer getContractType() { return contractType; }
    public void setContractType(Integer contractType) { this.contractType = contractType; }
    public String getContractTypeLabel() { return contractTypeLabel; }
    public void setContractTypeLabel(String contractTypeLabel) { this.contractTypeLabel = contractTypeLabel; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(BigDecimal basicSalary) { this.basicSalary = basicSalary; }
    public Integer getSalaryType() { return salaryType; }
    public void setSalaryType(Integer salaryType) { this.salaryType = salaryType; }
    public String getSalaryTypeLabel() { return salaryTypeLabel; }
    public void setSalaryTypeLabel(String salaryTypeLabel) { this.salaryTypeLabel = salaryTypeLabel; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public boolean isCurrent() { return current; }
    public void setCurrent(boolean current) { this.current = current; }
}
