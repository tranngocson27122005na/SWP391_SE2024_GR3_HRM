package com.hrm.dto.request;

public class ContractFormRequest {
    private Long employeeId;
    private Integer contractType;
    private String startDate;
    private String endDate;
    private String basicSalary;
    private Integer salaryType;

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public Integer getContractType() { return contractType; }
    public void setContractType(Integer contractType) { this.contractType = contractType; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getBasicSalary() { return basicSalary; }
    public void setBasicSalary(String basicSalary) { this.basicSalary = basicSalary; }
    public Integer getSalaryType() { return salaryType; }
    public void setSalaryType(Integer salaryType) { this.salaryType = salaryType; }
}
