package com.bobost.panel_back_end.exception.user;

import com.bobost.panel_back_end.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectPassword(IncorrectPasswordException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
                "Incorrect password", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidOTPCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOTPCode(InvalidOTPCodeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
                "Invalid OTP code", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(OTPRequiredException.class)
    public ResponseEntity<ErrorResponse> handleOTPRequired(OTPRequiredException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                "OTP code required", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PasswordRequirementNotMetException.class)
    public ResponseEntity<ErrorResponse> handlePasswordRequirementNotMet(PasswordRequirementNotMetException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Password requirement not met", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameRequirementsNotMetException.class)
    public ResponseEntity<ErrorResponse> handleUsernameRequirementsNotMet(UsernameRequirementsNotMetException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Username requirements not met", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserDoesNotHaveTOTPException.class)
    public ResponseEntity<ErrorResponse> handleUserDoesNotHaveTOTP(UserDoesNotHaveTOTPException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "User does not have OTP", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserHasAlreadyTOTPException.class)
    public ResponseEntity<ErrorResponse> handleUserHasAlreadyTOTP(UserHasAlreadyTOTPException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "User already has OTP", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "User not found", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<ErrorResponse> handleUserNotAuthenticated(UserNotAuthenticatedException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
                "User not authenticated", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}
