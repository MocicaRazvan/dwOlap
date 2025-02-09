package com.mocicarazvan.dwolap.enums;

public enum PlataTip implements EnumGetValue {
    CHITANTA("chitanta"),
    FACTURA("factura"),
    AVANS("avans"),
    REST("rest");
    private final String value;

    PlataTip(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
