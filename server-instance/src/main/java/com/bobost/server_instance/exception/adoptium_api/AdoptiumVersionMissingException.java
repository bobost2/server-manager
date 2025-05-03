package com.bobost.server_instance.exception.adoptium_api;

public class AdoptiumVersionMissingException extends RuntimeException {
    public AdoptiumVersionMissingException(String message) {
        super(message);
    }
}
