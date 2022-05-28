package com.jay.shoppingmall.exception.exceptions;

public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException(final String message) {
        super(message);
    }

    public TokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}