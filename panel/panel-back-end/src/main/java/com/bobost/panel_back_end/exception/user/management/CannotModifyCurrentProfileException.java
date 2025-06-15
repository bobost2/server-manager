package com.bobost.panel_back_end.exception.user.management;

public class CannotModifyCurrentProfileException extends RuntimeException {
    public CannotModifyCurrentProfileException(String message) {
        super(message);
    }
}
