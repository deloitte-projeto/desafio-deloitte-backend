package com.projeto.deloitte.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AvailabilityConflictException extends RuntimeException {
    public AvailabilityConflictException(String message) {
        super(message);
    }
} 