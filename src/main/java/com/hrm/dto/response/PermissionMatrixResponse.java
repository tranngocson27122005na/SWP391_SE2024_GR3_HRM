package com.hrm.dto.response;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PermissionMatrixResponse {
    private List<MatrixPositionRow> positions = new ArrayList<>();
    private List<MatrixPermissionCol> permissions = new ArrayList<>();
    /** Tokens "positionId_permissionId" currently granted. */
    private Set<String> grantedKeys = new HashSet<>();

    public List<MatrixPositionRow> getPositions() {
        return positions;
    }

    public void setPositions(List<MatrixPositionRow> positions) {
        this.positions = positions != null ? positions : new ArrayList<>();
    }

    public List<MatrixPermissionCol> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<MatrixPermissionCol> permissions) {
        this.permissions = permissions != null ? permissions : new ArrayList<>();
    }

    public Set<String> getGrantedKeys() {
        return grantedKeys;
    }

    public void setGrantedKeys(Set<String> grantedKeys) {
        this.grantedKeys = grantedKeys != null ? grantedKeys : new HashSet<>();
    }

    public static class MatrixPositionRow {
        private Integer positionId;
        private String positionCode;
        private String positionName;
        private String departmentName;
        private Integer dataScope;
        private String dataScopeLabel;

        public Integer getPositionId() {
            return positionId;
        }

        public void setPositionId(Integer positionId) {
            this.positionId = positionId;
        }

        public String getPositionCode() {
            return positionCode;
        }

        public void setPositionCode(String positionCode) {
            this.positionCode = positionCode;
        }

        public String getPositionName() {
            return positionName;
        }

        public void setPositionName(String positionName) {
            this.positionName = positionName;
        }

        public String getDepartmentName() {
            return departmentName;
        }

        public void setDepartmentName(String departmentName) {
            this.departmentName = departmentName;
        }

        public Integer getDataScope() {
            return dataScope;
        }

        public void setDataScope(Integer dataScope) {
            this.dataScope = dataScope;
        }

        public String getDataScopeLabel() {
            return dataScopeLabel;
        }

        public void setDataScopeLabel(String dataScopeLabel) {
            this.dataScopeLabel = dataScopeLabel;
        }
    }

    public static class MatrixPermissionCol {
        private Integer permissionId;
        private String permissionName;
        private String resource;
        private Byte action;
        private String actionLabel;

        public Integer getPermissionId() {
            return permissionId;
        }

        public void setPermissionId(Integer permissionId) {
            this.permissionId = permissionId;
        }

        public String getPermissionName() {
            return permissionName;
        }

        public void setPermissionName(String permissionName) {
            this.permissionName = permissionName;
        }

        public String getResource() {
            return resource;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }

        public Byte getAction() {
            return action;
        }

        public void setAction(Byte action) {
            this.action = action;
        }

        public String getActionLabel() {
            return actionLabel;
        }

        public void setActionLabel(String actionLabel) {
            this.actionLabel = actionLabel;
        }
    }
}
