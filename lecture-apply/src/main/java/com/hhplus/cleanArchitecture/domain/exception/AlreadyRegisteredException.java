package com.hhplus.cleanArchitecture.domain.exception;

public class AlreadyRegisteredException extends RuntimeException{
    public AlreadyRegisteredException(String message) {
        super(message);
    }
}
