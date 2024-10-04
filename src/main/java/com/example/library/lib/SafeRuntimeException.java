package com.example.library.lib;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * SafeApiException is a custom exception that extends RuntimeException. It is used to handle exceptions in the
 * application whose content is safe to be known by the consumers of the API.
 */
public class SafeRuntimeException extends RuntimeException {
    /**
     * Creates a new exception that represents an intentional and controlled interruption of the execution flow.
     *
     * @param message - the exception message
     */
    public SafeRuntimeException(@NonNull String message) {
        super(message);
    }

    /**
     * Creates a new exception that represents a non-intentional interruption of the execution flow.
     *
     * @param message - the exception message
     * @param cause   - the exception that caused this exception
     */
    public SafeRuntimeException(@Nullable String message, @NonNull Throwable cause) {
        super(message, cause);
    }
}