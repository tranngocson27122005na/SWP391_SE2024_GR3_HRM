package com.hrm.dto.request;

import java.util.ArrayList;
import java.util.List;

public class PermissionMatrixUpdateRequest {
    /** Tokens "positionId_permissionId" checked on the form. */
    private List<String> assignments = new ArrayList<>();

    public List<String> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<String> assignments) {
        this.assignments = assignments != null ? assignments : new ArrayList<>();
    }
}
