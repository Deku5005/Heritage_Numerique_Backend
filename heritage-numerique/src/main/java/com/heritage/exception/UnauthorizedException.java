package com.heritage.exception;

/**
 * Exception lancée lorsqu'un utilisateur n'est pas autorisé à effectuer une action.
 */
public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
    
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}