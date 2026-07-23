package com.hrm.infrastructure.exception;

/**
 * Thrown by the Service layer when the current user is not allowed
 * to access the requested data or perform the requested action.
 */
public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
