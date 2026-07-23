package com.hrm.persistence.entity.enums;

public enum Gender {
    MALE(1, "Nam"),
    FEMALE(2, "Nữ"),
    OTHER(3, "Khác");

    private final int code;
    private final String label;

    Gender(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static Gender fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (Gender g : values()) {
            if (g.code == code) {
                return g;
            }
        }
        return null;
    }

    public static String labelOf(Integer code) {
        Gender g = fromCode(code);
        return g == null ? "—" : g.label;
    }
}
