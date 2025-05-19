package com.bobost.panel_back_end.exception.user;

import org.springframework.security.core.AuthenticationException;

public class OTPRequiredException extends AuthenticationException {
    public OTPRequiredException(String message) {
        super(message);
    }
}
