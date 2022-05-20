package com.jay.shoppingmall.exception.exceptions;

public class UserDuplicatedException extends RuntimeException {

    public UserDuplicatedException(final String message) {
        super(message);
    }
    public UserDuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
