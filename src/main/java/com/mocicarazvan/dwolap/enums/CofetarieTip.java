package com.mocicarazvan.dwolap.enums;


public enum CofetarieTip implements EnumGetValue {
    FARA_SERVIRE("fara-servire"),
    CU_SERVIRE("cu-servire");

    private final String value;

    CofetarieTip(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
