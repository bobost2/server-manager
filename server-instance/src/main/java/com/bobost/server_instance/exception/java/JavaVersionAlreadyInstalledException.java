package com.bobost.server_instance.exception.java;

public class JavaVersionAlreadyInstalledException extends RuntimeException {
    public JavaVersionAlreadyInstalledException(String message) {
        super(message);
    }
}
