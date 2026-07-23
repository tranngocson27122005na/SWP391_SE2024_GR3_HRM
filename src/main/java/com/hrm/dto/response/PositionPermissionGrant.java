package com.hrm.dto.response;

/**
 * One position↔permission grant row for PositionPermissionMatrix cache.
 */
public class PositionPermissionGrant {
    private Integer positionId;
    private String permissionName;
    private String resource;
    private Byte action;

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
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
}
