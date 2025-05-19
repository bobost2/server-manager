package com.bobost.panel_back_end.exception.user;

import org.springframework.security.core.AuthenticationException;

public class InvalidOTPCodeException extends AuthenticationException {
    public InvalidOTPCodeException(String message) {
        super(message);
    }
}
