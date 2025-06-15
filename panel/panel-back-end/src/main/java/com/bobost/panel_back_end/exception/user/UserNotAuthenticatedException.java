package com.bobost.panel_back_end.exception.user;

public class UserNotAuthenticatedException extends RuntimeException {
    public UserNotAuthenticatedException(String message) {
        super(message);
    }
}
