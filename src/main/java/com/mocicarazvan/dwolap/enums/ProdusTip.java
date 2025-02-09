package com.mocicarazvan.dwolap.enums;

public enum ProdusTip implements EnumGetValue {
    PRAJITURA("prajitura"),
    TORT("tort"),
    SUC("suc");

    private final String value;

    ProdusTip(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
