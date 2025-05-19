package com.bobost.panel_back_end.exception.user;

public class PasswordRequirementNotMetException extends RuntimeException {
    public PasswordRequirementNotMetException(String message) {
        super(message);
    }
}
