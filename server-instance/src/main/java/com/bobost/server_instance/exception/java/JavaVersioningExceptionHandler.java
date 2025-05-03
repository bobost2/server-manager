package com.bobost.server_instance.exception.java;

import com.bobost.server_instance.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class JavaVersioningExceptionHandler {

    @ExceptionHandler(JavaFolderAccessException.class)
    public ResponseEntity<ErrorResponse> handleFolderAccessEx(JavaFolderAccessException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Java folder access error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(JavaVersionAlreadyInstalledException.class)
    public ResponseEntity<ErrorResponse> handleVersionAlreadyInstalled(JavaVersionAlreadyInstalledException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Java version already installed", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JavaVersionNotInstalledException.class)
    public ResponseEntity<ErrorResponse> handleVersionNotInstalled(JavaVersionNotInstalledException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                "Java version not installed", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JavaVersionInstallationFailureException.class)
    public ResponseEntity<ErrorResponse> handleVersionInstallationFailure(JavaVersionInstallationFailureException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Java version installation failure", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(JavaVersionMissingException.class)
    public ResponseEntity<ErrorResponse> handleVersionMissing(JavaVersionMissingException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                "Java version does not exist", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JavaVersionRemovalFailureException.class)
    public ResponseEntity<ErrorResponse> handleVersionRemovalFailure(JavaVersionRemovalFailureException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Java version removal failure", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(JavaVersionSelectedException.class)
    public ResponseEntity<ErrorResponse> handleVersionSelected(JavaVersionSelectedException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Java version already selected", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
