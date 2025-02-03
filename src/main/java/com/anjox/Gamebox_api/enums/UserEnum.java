package com.anjox.Gamebox_api.enums;

public enum UserEnum {
    USER("USER"),
    ADM("ADM");

    private final String value;
    UserEnum(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
