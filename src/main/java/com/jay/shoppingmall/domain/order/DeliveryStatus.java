package com.jay.shoppingmall.domain.order;

public enum DeliveryStatus {
    WAITING_FOR_PAYMENT("결제 대기 중"),
    PAYMENT_DONE("결제 완료"),
    SHIPPED("배송 출발"),
    DELIVERING("배송 중"),
    DELIVERED("배송 완료"),
    ORDER_CANCEL_REQUEST("주문 취소 요청"),
    ORDER_CANCEL_DONE("주문 취소 완료"),
    RETURN_REQUEST("반품 요청"),
    RETURNING("반품 진행 중"),
    RETURNED("반품 완료"),
    PAYMENT_CANCEL_REQUEST("결제 취소 요청"),
    PAYMENT_CANCEL_DONE("결제 취소 완료"),
    ;

    private final String value;

    DeliveryStatus(String value) {
        this.value = value;
    }
    public String value() {
        return value;
    }
}
