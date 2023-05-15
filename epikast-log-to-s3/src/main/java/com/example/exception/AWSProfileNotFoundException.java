package com.example.exception;

public class AWSProfileNotFoundException extends IllegalArgumentException{
    public AWSProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
