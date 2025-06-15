package com.bobost.panel_back_end.exception.role;

public class RoleNameAlreadyExistsException extends RuntimeException {
    public RoleNameAlreadyExistsException(String message) {
        super(message);
    }
}
