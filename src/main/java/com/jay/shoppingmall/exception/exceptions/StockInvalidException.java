package com.jay.shoppingmall.exception.exceptions;

public class StockInvalidException extends RuntimeException{

    public StockInvalidException(String message) {
        super(message);
    }

    public StockInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
