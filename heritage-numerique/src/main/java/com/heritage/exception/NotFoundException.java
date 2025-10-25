package com.heritage.exception;

/**
 * Exception lancée lorsqu'une ressource n'est pas trouvée.
 */
public class NotFoundException extends RuntimeException {
    
    public NotFoundException(String message) {
        super(message);
    }
    
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}