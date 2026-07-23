package com.hrm.infrastructure.exception;

/**
 * Thrown by the Service layer when a business validation rule fails
 * (e.g. duplicate code, invalid state). Message is safe to show to end users.
 */
public class ValidationException extends BusinessException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
