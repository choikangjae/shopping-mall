package com.jay.shoppingmall.domain.order;

public enum DeliveryStatus {
    WAITING_FOR_PAYMENT,
    PAYMENT_DONE,
    SHIPPED,
    DELIVERING,
    DELIVERED,
    ORDER_CANCEL_REQUEST,
    ORDER_CANCEL_DONE,
    RETURN_REQUEST,
    RETURNING,
    RETURNED,
    PAYMENT_CANCEL_REQUEST,
    PAYMENT_CANCEL_DONE,
}
