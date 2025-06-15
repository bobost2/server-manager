package com.bobost.panel_back_end.exception.role;

public class RoleHasBeenAssignedException extends RuntimeException {
    public RoleHasBeenAssignedException(String message) {
        super(message);
    }
}
