package com.project.common;

import java.util.List;
import org.springframework.http.HttpStatus;


public record ApiErrorResponse(boolean success, int status, String code, String message,
                               List<FieldError> fieldErrors) {
    public record FieldError(String field, String message) { }

    public static ApiErrorResponse of(HttpStatus status, String message) {
        return new ApiErrorResponse(false, status.value(), codeFor(status), message, List.of());
    }

    public static ApiErrorResponse validation(HttpStatus status, String message, List<FieldError> fieldErrors) {
        return new ApiErrorResponse(false, status.value(), codeFor(status), message, List.copyOf(fieldErrors));
    }

    private static String codeFor(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST -> "VALIDATION_ERROR";
            case UNAUTHORIZED -> "UNAUTHORIZED";
            case FORBIDDEN -> "FORBIDDEN";
            case NOT_FOUND -> "NOT_FOUND";
            case CONFLICT -> "CONFLICT";
            case PAYLOAD_TOO_LARGE -> "PAYLOAD_TOO_LARGE";
            default -> "INTERNAL_ERROR";
        };
    }
}
