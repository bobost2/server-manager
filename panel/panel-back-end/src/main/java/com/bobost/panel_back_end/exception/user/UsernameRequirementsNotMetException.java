package com.bobost.panel_back_end.exception.user;

public class UsernameRequirementsNotMetException extends RuntimeException {
    public UsernameRequirementsNotMetException(String message) {
        super(message);
    }
}
