package com.jay.shoppingmall.exception.exceptions;

public class MailNotSentException extends RuntimeException {

    public MailNotSentException(final String message) {
        super(message);
    }

    public MailNotSentException(String message, Throwable cause) {
        super(message, cause);
    }
}
