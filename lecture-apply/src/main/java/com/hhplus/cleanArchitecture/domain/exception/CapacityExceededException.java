package com.hhplus.cleanArchitecture.domain.exception;

public class CapacityExceededException extends RuntimeException{
    public CapacityExceededException(String message) {
        super(message);
    }
}
