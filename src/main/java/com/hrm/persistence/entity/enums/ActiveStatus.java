package com.hrm.persistence.entity.enums;

/** Soft-status TINYINT (0.hrmdb.sql / ADR-0002). */
public enum ActiveStatus {
    INACTIVE(0),
    ACTIVE(1);

    private final int code;

    ActiveStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public byte toByte() {
        return (byte) code;
    }

    public static ActiveStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ActiveStatus s : values()) {
            if (s.code == code) {
                return s;
            }
        }
        return null;
    }

    public static ActiveStatus fromCode(Byte code) {
        return code == null ? null : fromCode(code.intValue());
    }
}
