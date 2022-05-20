package com.jay.shoppingmall.exception.exceptions;

public class PasswordInvalidException extends RuntimeException {

    public PasswordInvalidException(final String message) {
        super(message);
    }

    public PasswordInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
