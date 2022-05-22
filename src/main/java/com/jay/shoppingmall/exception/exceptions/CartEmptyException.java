package com.jay.shoppingmall.exception.exceptions;

import com.jay.shoppingmall.dto.response.CartOrderResponse;

public class CartEmptyException extends RuntimeException {

    public CartEmptyException(String message) {
        super(message);
    }

    public CartEmptyException(String message, Throwable cause) {
        super(message, cause);
    }
}
