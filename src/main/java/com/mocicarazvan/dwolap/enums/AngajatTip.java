package com.mocicarazvan.dwolap.enums;

public enum AngajatTip implements EnumGetValue {
    SOFER_LIVRATOR("sofer_livrator"),
    COFETAR("cofetar"),
    CHELNER("chelner");

    private final String value;

    AngajatTip(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
