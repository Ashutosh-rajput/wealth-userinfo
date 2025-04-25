package com.WealthManager.UserInfo.exception;

import com.nimbusds.jwt.proc.BadJWTException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.security.SignatureException;
import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenExpiredException(
            RefreshTokenExpiredException ex, HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handlesSecurityException(Exception ex, HttpServletRequest request) {
        HttpStatus status;
        String message;

        if (ex instanceof BadCredentialsException) {
            status = HttpStatus.UNAUTHORIZED;
            message = "Authentication Failure: " + ex.getMessage();
        } else if (ex instanceof AccessDeniedException) {
            status = HttpStatus.FORBIDDEN;
            message = "Not Authorized: " + ex.getMessage();
        } else if (ex instanceof SignatureException) {
            status = HttpStatus.FORBIDDEN;
            message = "Invalid JWT Signature: " + ex.getMessage();
        } else if (ex instanceof BadJWTException) {
            status = HttpStatus.UNAUTHORIZED;
            message = "Invalid or Expired JWT: " + ex.getMessage();
        } else if (ex instanceof JwtValidationException) {
            status = HttpStatus.UNAUTHORIZED;
            message = "Expired JWT: " + ex.getMessage();
        } else if (ex instanceof JwtException) {
            status = HttpStatus.UNAUTHORIZED;
            message = "Invalid or Expired JWT: " + ex.getMessage();
        }

        else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = ex.getMessage() != null ? ex.getMessage() : "Unexpected error occurred";
        }

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                message,
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, status);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidArgument(MethodArgumentNotValidException ex, HttpServletRequest request) {
        StringBuilder combinedMessage = new StringBuilder("Validation failed: ");
        ex.getBindingResult().getFieldErrors().forEach(error ->
                combinedMessage.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ")
        );

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                combinedMessage.toString(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCode(HttpStatus.NOT_FOUND.value());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setTimestamp(new Date().toInstant().toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCode(HttpStatus.CONFLICT.value());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setTimestamp(new Date().toInstant().toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailAlreadyExists.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExists ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCode(HttpStatus.CONFLICT.value());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setTimestamp(new Date().toInstant().toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PhoneNumberAlreadyExists.class)
    public ResponseEntity<ErrorResponse> handlePhoneNumberAlreadyExistsException(PhoneNumberAlreadyExists ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCode(HttpStatus.CONFLICT.value());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setTimestamp(new Date().toInstant().toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ReportAlreadyExitsorConflict.class)
    public ResponseEntity<ErrorResponse> handleReportAlreadyExitsorConflict(ReportAlreadyExitsorConflict ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCode(HttpStatus.CONFLICT.value());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setTimestamp(new Date().toInstant().toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenNotFoundException(RefreshTokenNotFoundException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCode(HttpStatus.NOT_FOUND.value());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setTimestamp(new Date().toInstant().toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidFileTypeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFileTypeException(InvalidFileTypeException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setTimestamp(new Date().toInstant().toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCode(HttpStatus.NOT_FOUND.value());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setTimestamp(new Date().toInstant().toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleUserNotVerifiedException(UserNotVerifiedException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCode(HttpStatus.FORBIDDEN.value());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setTimestamp(new Date().toInstant().toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }


}
