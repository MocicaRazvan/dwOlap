package com.mocicarazvan.dwolap.enums;


public enum SexTip implements EnumGetValue {
    M("M"),
    F("F");

    private final String value;

    SexTip(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

}
