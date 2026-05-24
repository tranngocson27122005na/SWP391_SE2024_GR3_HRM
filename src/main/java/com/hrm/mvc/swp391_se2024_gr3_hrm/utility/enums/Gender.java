package com.hrm.mvc.swp391_se2024_gr3_hrm.utility.enums;

public enum Gender {
    FEMALE(0),
    MALE(1);

    private final int value;

    Gender(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Gender from(Integer value) {
        for (Gender gender : values()) {
            if (gender.value == value) {
                return gender;
            }
        }
        return null;
    }

    public String getDisplayName() {
        return this == MALE ? "Male" : "Female";
    }
}
