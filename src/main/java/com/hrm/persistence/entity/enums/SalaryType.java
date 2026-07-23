package com.hrm.persistence.entity.enums;

/** contract.salary_type */
public enum SalaryType {
    MONTHLY(1, "Theo tháng"),
    HOURLY(2, "Theo giờ");

    private final int code;
    private final String label;

    SalaryType(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public byte toByte() {
        return (byte) code;
    }

    public static SalaryType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (SalaryType t : values()) {
            if (t.code == code) {
                return t;
            }
        }
        return null;
    }

    public static String labelOf(Integer code) {
        SalaryType t = fromCode(code);
        return t == null ? "" : t.label;
    }
}
