package com.bobost.server_instance.exception.server;

public class JarUploadFailureException extends RuntimeException {
    public JarUploadFailureException(String message) {
        super(message);
    }
}
