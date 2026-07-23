package com.hrm.persistence.entity.enums;

/** contract.contract_type — MVP 2 loại (ADR-0002). */
public enum ContractType {
    PROBATION(1, "Thử việc"),
    OFFICIAL(2, "Chính thức");

    private final int code;
    private final String label;

    ContractType(int code, String label) {
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

    public static ContractType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ContractType t : values()) {
            if (t.code == code) {
                return t;
            }
        }
        return null;
    }

    public static String labelOf(Integer code) {
        ContractType t = fromCode(code);
        return t == null ? "" : t.label;
    }
}
