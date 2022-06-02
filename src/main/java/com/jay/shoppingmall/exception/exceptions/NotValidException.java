package com.jay.shoppingmall.exception.exceptions;

public class NotValidException extends RuntimeException {

    public NotValidException(String message) {
        super(message);
    }

    public NotValidException(String message, Throwable cause) {
        super(message, cause);
    }
}
