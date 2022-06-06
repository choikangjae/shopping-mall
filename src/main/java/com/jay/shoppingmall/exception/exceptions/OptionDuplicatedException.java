package com.jay.shoppingmall.exception.exceptions;

public class OptionDuplicatedException extends RuntimeException {
    public OptionDuplicatedException(String message) {
        super(message);
    }

    public OptionDuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
