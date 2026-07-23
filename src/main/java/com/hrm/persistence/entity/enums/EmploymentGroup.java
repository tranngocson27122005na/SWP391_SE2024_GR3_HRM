package com.hrm.persistence.entity.enums;

public enum EmploymentGroup {
    OFFICE(1, "Văn phòng"),
    FACTORY(2, "Nhà máy");

    private final int code;
    private final String label;

    EmploymentGroup(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static EmploymentGroup fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (EmploymentGroup g : values()) {
            if (g.code == code) {
                return g;
            }
        }
        return null;
    }

    public static String labelOf(Integer code) {
        EmploymentGroup g = fromCode(code);
        return g == null ? "—" : g.label;
    }
}
