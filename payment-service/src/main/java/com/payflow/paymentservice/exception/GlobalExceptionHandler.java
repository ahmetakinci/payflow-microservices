package com.payflow.paymentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String STATUS_KEY = "status";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String ERROR_KEY = "error";
    private static final String ERRORS_KEY = "errors";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        ERRORS_KEY, errors,
                        STATUS_KEY, 400,
                        TIMESTAMP_KEY, LocalDateTime.now().toString()
                )
        );
    }

    @ExceptionHandler(AccountServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleUserServiceUnavailable(AccountServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
                Map.of(
                        ERROR_KEY, ex.getMessage(),
                        STATUS_KEY, 503,
                        TIMESTAMP_KEY, LocalDateTime.now().toString()
                )
        );
    }
}