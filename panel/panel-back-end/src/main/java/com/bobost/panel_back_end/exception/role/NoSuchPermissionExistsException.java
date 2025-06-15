package com.bobost.panel_back_end.exception.role;

public class NoSuchPermissionExistsException extends RuntimeException {
    public NoSuchPermissionExistsException(String message) {
        super(message);
    }
}
