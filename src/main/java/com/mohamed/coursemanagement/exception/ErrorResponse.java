package com.mohamed.coursemanagement.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        int status,
        String error,
        String message,
        LocalDateTime timestamp,
        Map<String, String> fieldErrors
) {

    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, LocalDateTime.now(), null);
    }

    public ErrorResponse(int status, String error, String message, Map<String, String> fieldErrors) {
        this(status, error, message, LocalDateTime.now(), fieldErrors);
    }
}
