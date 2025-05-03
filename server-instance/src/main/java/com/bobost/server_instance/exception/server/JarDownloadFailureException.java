package com.bobost.server_instance.exception.server;

public class JarDownloadFailureException extends RuntimeException {
    public JarDownloadFailureException(String message) {
        super(message);
    }
}
