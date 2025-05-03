package com.bobost.server_instance.exception.adoptium_api;

import com.bobost.server_instance.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AdoptiumAPIExceptionHandler {

    @ExceptionHandler(AdoptiumVersionMissingException.class)
    public ResponseEntity<ErrorResponse> handleMissingVersion(AdoptiumVersionMissingException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                "Java version not found", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AdoptiumAPIException.class)
    public ResponseEntity<ErrorResponse> handleAdoptiumAPIException(AdoptiumAPIException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Adoptium API error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
