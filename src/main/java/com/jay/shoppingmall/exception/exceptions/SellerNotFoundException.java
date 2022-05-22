package com.jay.shoppingmall.exception.exceptions;

public class SellerNotFoundException extends RuntimeException {
    public SellerNotFoundException(final String message) {
        super(message);
    }

    public SellerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
