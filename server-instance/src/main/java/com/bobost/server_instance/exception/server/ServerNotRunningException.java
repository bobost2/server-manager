package com.bobost.server_instance.exception.server;

public class ServerNotRunningException extends RuntimeException {
    public ServerNotRunningException(String message) {
        super(message);
    }
}
