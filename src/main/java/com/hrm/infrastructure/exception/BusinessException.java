package com.hrm.infrastructure.exception;

/**
 * Thrown by the Service layer when a business rule or validation fails.
 * The message is safe to display back to the end user.
 * Controllers catch this and forward the message to the view.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
