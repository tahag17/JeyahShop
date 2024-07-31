package com.jeyah.jeyahshopapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum ErrorCodes {

    NO_CODE(0, NOT_IMPLEMENTED, "No code"),
    INCORRECT_CURRENT_PASSWORD(300, BAD_REQUEST, "Incorrect password"),
    ACCOUNT_LOCKED(302, FORBIDDEN, "Account locked"),
    ;

    @Getter
    private int code;
    @Getter
    private String description;
    @Getter
    private HttpStatus httpStatus;

    ErrorCodes(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
