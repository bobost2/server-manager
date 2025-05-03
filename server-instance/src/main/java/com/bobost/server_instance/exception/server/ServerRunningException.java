package com.bobost.server_instance.exception.server;

public class ServerRunningException extends RuntimeException {
    public ServerRunningException(String message) {
        super(message);
    }
}
