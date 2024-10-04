package com.example.library.aspect;

import com.example.library.lib.SafeRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Aspect to handle exceptions thrown by the REST controllers.
 */
@RestControllerAdvice
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {
    /**
     * The environment object to check if the test profile is active.
     */
    @NonNull
    private final Environment environment;

    /**
     * Constructor for the RestControllerExceptionHandler.
     *
     * @param environment The environment object to check if the test profile is active
     */
    @Autowired
    public RestControllerExceptionHandler(@NonNull Environment environment) {
        this.environment = environment;
    }

    /**
     * Handles MethodValidationExceptions and returns a ResponseEntity with the exception message.
     *
     * @param ex      The MethodValidationException to handle
     * @param request The WebRequest that caused the exception
     * @return ResponseEntity with the exception message
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        Map<String, Map<String, String>> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles SafeRuntimeExceptions and returns a ResponseEntity with the exception message.
     *
     * @param ex      The SafeRuntimeException to handle
     * @param request The WebRequest that caused the exception
     * @return ResponseEntity with the exception message
     */
    @ExceptionHandler(SafeRuntimeException.class)
    public ResponseEntity<String> handleSafeException(SafeRuntimeException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), ex.getCause() == null ? HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles all other exceptions and returns a ResponseEntity with a generic error message and HTTP status code.
     *
     * @param ex      The exception to handle
     * @param request The WebRequest that caused the exception
     * @return ResponseEntity with a generic error message and HTTP status code
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        // Check if the test profile is active.
        boolean isTestProfileActive = Arrays.asList(this.environment.getActiveProfiles()).contains("test");
        // Return a generic error message if the test profile is not active.
        String errorMessage = isTestProfileActive ? ex.getMessage() : "An unexpected error occurred";
        // Return an internal server error if the test profile is active, otherwise return a bad request.
        HttpStatus httpStatus = isTestProfileActive ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.BAD_REQUEST;
        // Return the response entity with the error message and HTTP status.
        return new ResponseEntity<>(errorMessage, httpStatus);
    }
}