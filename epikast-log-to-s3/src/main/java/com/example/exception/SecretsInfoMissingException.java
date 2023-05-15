package com.example.exception;

public class SecretsInfoMissingException extends RuntimeException{
    public SecretsInfoMissingException(String message, Throwable cause) {
        super(message, cause);
    }
}
