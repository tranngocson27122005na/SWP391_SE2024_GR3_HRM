package com.hrm.mvc.swp391_se2024_gr3_hrm.utility.enums;

public enum ActiveStatus {
    INACTIVE(false),
    ACTIVE(true);

    private final boolean value;

    ActiveStatus(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public static ActiveStatus from(Boolean value) {
        return Boolean.TRUE.equals(value)
                ? ACTIVE
                : INACTIVE;
    }
}
