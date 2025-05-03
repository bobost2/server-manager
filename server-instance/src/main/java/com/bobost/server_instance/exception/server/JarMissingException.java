package com.bobost.server_instance.exception.server;

public class JarMissingException extends RuntimeException {
    public JarMissingException(String message) {
        super(message);
    }
}
