package com.hrm.mvc.swp391_se2024_gr3_hrm.utility.enums;

public enum Role {
    COMMON(1, "Common"),
    ADMIN(2, "Admin"),
    ADMIN_ADVANCED(3, "Admin Advanced");

    private final int id;
    private final String displayName;

    Role(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Role fromId(Integer id) {
        for (Role role : values()) {
            if (role.id == id) {
                return role;
            }
        }
        return null;
    }
}
