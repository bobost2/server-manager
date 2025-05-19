package com.bobost.panel_back_end.exception.user;

public class UserDoesNotHaveTOTPException extends RuntimeException {
    public UserDoesNotHaveTOTPException(String message) {
        super(message);
    }
}
