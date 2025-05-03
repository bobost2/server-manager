package com.bobost.server_instance.exception.java;

public class JavaVersionNotInstalledException extends RuntimeException {
    public JavaVersionNotInstalledException(String message) {
        super(message);
    }
}
