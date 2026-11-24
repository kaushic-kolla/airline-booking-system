
package com.airline.booking.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle errors such as flight not found
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(
            ResponseStatusException ex,
            HttpServletRequest request) {

        int status = ex.getStatusCode().value();

        String message = ex.getReason() != null
                ? ex.getReason()
                : "Request could not be processed";

        ApiError error = new ApiError(
                status,
                HttpStatus.valueOf(status).getReasonPhrase(),
                message,
                request.getRequestURI(),
                Instant.now()
        );

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(error);
    }

    // Handle invalid request body fields
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(field -> field.getField()
                        + ": " + field.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ApiError error = new ApiError(
                400,
                "Bad Request",
                message,
                request.getRequestURI(),
                Instant.now()
        );

        return ResponseEntity
                .badRequest()
                .body(error);
    }
}
