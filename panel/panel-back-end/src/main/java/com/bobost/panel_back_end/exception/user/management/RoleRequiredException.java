package com.bobost.panel_back_end.exception.user.management;

public class RoleRequiredException extends RuntimeException {
    public RoleRequiredException(String message) {
        super(message);
    }
}
