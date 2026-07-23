package com.hrm.dto.request;

public class EmployeeFormRequest {
    private String employeeCode;
    private String fullName;
    private Integer gender;
    private String birthDate;
    private String bankAccount;
    private Long positionId;
    private Integer employmentGroup;
    private String joiningDate;

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }
    public Long getPositionId() { return positionId; }
    public void setPositionId(Long positionId) { this.positionId = positionId; }
    public Integer getEmploymentGroup() { return employmentGroup; }
    public void setEmploymentGroup(Integer employmentGroup) { this.employmentGroup = employmentGroup; }
    public String getJoiningDate() { return joiningDate; }
    public void setJoiningDate(String joiningDate) { this.joiningDate = joiningDate; }
}
