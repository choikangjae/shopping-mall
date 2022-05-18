package com.jay.shoppingmall.exception;

public class AgreeException extends RuntimeException {
    public AgreeException(String message) {
        super(message);
    }

    public AgreeException(String message, Throwable cause) {
        super(message, cause);
    }
}
