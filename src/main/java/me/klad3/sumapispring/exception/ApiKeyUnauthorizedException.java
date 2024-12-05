package me.klad3.sumapispring.exception;

public class ApiKeyUnauthorizedException extends RuntimeException {
    public ApiKeyUnauthorizedException(String message) {
        super(message);
    }
}

