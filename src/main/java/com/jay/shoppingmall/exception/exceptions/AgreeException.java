package com.jay.shoppingmall.exception.exceptions;

public class AgreeException extends RuntimeException {
    public AgreeException(String message) {
        super(message);
    }

    public AgreeException(String message, Throwable cause) {
        super(message, cause);
    }
}
