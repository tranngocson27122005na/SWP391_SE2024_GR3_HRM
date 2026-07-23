package com.hrm.persistence.entity.enums;

/**
 * TINYINT codes for permission.action (ADR-0002). Numbers match sql/0.seed_data.sql.
 */
public enum PermissionAction {
    CREATE(1),
    READ(2),
    UPDATE(3),
    DELETE(4),
    IMPORT(5),
    EXPORT(6),
    APPROVE(7),
    REJECT(8),
    SUBMIT(9),
    CANCEL(10);

    private final int code;

    PermissionAction(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static PermissionAction fromCode(int code) {
        for (PermissionAction a : values()) {
            if (a.code == code) {
                return a;
            }
        }
        return null;
    }

    public static PermissionAction fromCode(Byte code) {
        return code == null ? null : fromCode(code.intValue());
    }
}
