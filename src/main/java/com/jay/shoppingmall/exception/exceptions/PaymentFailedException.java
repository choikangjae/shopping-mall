package com.jay.shoppingmall.exception.exceptions;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(final String message) {
        super(message);
    }

    public PaymentFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
