package com.jay.shoppingmall.exception.exceptions;

public class QnaException extends RuntimeException {

    public QnaException(final String message) {
        super(message);
    }

    public QnaException(String message, Throwable cause) {
        super(message, cause);
    }
}
