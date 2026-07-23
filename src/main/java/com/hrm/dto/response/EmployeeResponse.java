package com.hrm.dto.response;

import java.util.Date;

public class EmployeeResponse {
    private Long employeeId;
    private String employeeCode;
    private String fullName;
    private Integer gender;
    private String genderLabel;
    private Date birthDate;
    private String bankAccount;
    private Long positionId;
    private String positionName;
    private Long departmentId;
    private String departmentName;
    private Integer employmentGroup;
    private String employmentGroupLabel;
    private Date joiningDate;
    private Long currentContractId;
    private boolean working;
    private Integer status;
    private Date createdAt;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getGenderLabel() {
        return genderLabel;
    }

    public void setGenderLabel(String genderLabel) {
        this.genderLabel = genderLabel;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getEmploymentGroup() {
        return employmentGroup;
    }

    public void setEmploymentGroup(Integer employmentGroup) {
        this.employmentGroup = employmentGroup;
    }

    public String getEmploymentGroupLabel() {
        return employmentGroupLabel;
    }

    public void setEmploymentGroupLabel(String employmentGroupLabel) {
        this.employmentGroupLabel = employmentGroupLabel;
    }

    public Date getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(Date joiningDate) {
        this.joiningDate = joiningDate;
    }

    public Long getCurrentContractId() {
        return currentContractId;
    }

    public void setCurrentContractId(Long currentContractId) {
        this.currentContractId = currentContractId;
    }

    public boolean isWorking() {
        return working;
    }

    public void setWorking(boolean working) {
        this.working = working;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
