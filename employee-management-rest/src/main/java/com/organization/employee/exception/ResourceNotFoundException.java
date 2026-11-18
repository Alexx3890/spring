package com.organization.employee.exception;

/**
 * Exception thrown when a requested employee entity cannot be located.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

