package com.hrm.dto.request;

/**
 * Shared request DTO for create and update department.
 * Maps to Entity fields: code -> departmentCode, name -> departmentName.
 */
public class DepartmentFormRequest {

    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
