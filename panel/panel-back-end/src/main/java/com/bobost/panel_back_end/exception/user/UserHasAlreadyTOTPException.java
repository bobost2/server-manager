package com.bobost.panel_back_end.exception.user;

public class UserHasAlreadyTOTPException extends RuntimeException {
    public UserHasAlreadyTOTPException(String message) {
        super(message);
    }
}
