package com.jay.shoppingmall.exception.exceptions;

public class QuantityException extends RuntimeException {
    public QuantityException(final String message) {
        super(message);
    }

    public QuantityException(String message, Throwable cause) {
        super(message, cause);
    }
}
