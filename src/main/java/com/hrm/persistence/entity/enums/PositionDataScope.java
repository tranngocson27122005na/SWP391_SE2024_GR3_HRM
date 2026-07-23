package com.hrm.persistence.entity.enums;

/** job_position.data_scope TINYINT. */
public enum PositionDataScope {
    SELF(1, "Bản thân"),
    DEPARTMENT(2, "Phòng ban"),
    ALL(3, "Toàn công ty");

    private final int code;
    private final String label;

    PositionDataScope(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static PositionDataScope fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PositionDataScope s : values()) {
            if (s.code == code) {
                return s;
            }
        }
        return null;
    }

    public static String labelOf(Integer code) {
        PositionDataScope s = fromCode(code);
        return s == null ? "—" : s.label;
    }
}
