package com.hrm.dto.session;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Authenticated user context in HttpSession (docs common-auth / glossary).
 */
public class UserSession implements Serializable {

    private static final long serialVersionUID = 2L;

    private Long userId;
    private String username;
    private Set<String> roles = new HashSet<>();
    private Long employeeId;
    private Long positionId;
    private Long departmentId;
    private Integer dataScope;

    public UserSession() {
    }

    public boolean isAdmin() {
        return roles != null && roles.contains("ADMIN");
    }

    public boolean isUser() {
        return roles != null && roles.contains("USER");
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles != null ? roles : new HashSet<>();
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getDataScope() {
        return dataScope;
    }

    public void setDataScope(Integer dataScope) {
        this.dataScope = dataScope;
    }
}
