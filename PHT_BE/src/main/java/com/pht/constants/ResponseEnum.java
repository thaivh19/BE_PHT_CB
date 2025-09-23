package com.pht.constants;

public enum ResponseEnum {
    SUCCESS(0, "Success"),
    FAIL(1, "Fail"),
    NOT_FOUND(2, "Not Found"),
    ERROR(-1, "Error");

    public final int code;
    public final String message;

    ResponseEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
