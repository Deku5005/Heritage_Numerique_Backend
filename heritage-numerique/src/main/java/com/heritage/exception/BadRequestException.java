package com.heritage.exception;

/**
 * Exception lancée lorsqu'une requête est malformée ou invalide.
 */
public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String message) {
        super(message);
    }
    
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}