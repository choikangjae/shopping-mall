package com.jay.shoppingmall.exception.exceptions;

public class MoneyTransactionException extends RuntimeException {

    public MoneyTransactionException(final String message) {
        super(message);
    }

    public MoneyTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
