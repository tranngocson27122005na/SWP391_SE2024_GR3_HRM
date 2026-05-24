package com.hrm.mvc.swp391_se2024_gr3_hrm.utility.enums;

public enum MarriedStatus {
    SINGLE(0),
    MARRIED(1);

    private final int value;

    MarriedStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MarriedStatus from(Integer value) {
        for (MarriedStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }

    public String getDisplayName() {
        return this == MARRIED
                ? "Married"
                : "Single";
    }
}
