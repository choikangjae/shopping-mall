package com.jay.shoppingmall.domain.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeliveryStatus {
    PAYMENT_DONE("결제 완료", 10),
    SHIPPED("배송 출발", 11),
    DELIVERING("배송 중", 12),
    DELIVERED("배송 완료", 13),

    //다른 ENUM으로 관리할 것. priority를 위해
//    ORDER_CANCEL_REQUEST("주문 취소 요청"),
//    ORDER_CANCEL_DONE("주문 취소 완료"),
//
//    RETURN_REQUEST("반품 요청"),
//    RETURNING("반품 진행 중"),
//    RETURNED("반품 완료"),
//
//    WAITING_FOR_PAYMENT("결제 대기 중"),
//    PAYMENT_CANCEL_REQUEST("결제 취소 요청"),
//    PAYMENT_CANCEL_DONE("결제 취소 완료"),
    ;

    private final String value;
    private final int priority;

//    DeliveryStatus(String value, int priority) {
//        this.value = value;
//        this.priority = priority;
//    }
//    public String value() {
//        return value;
//    }
//    public int priority() {
//        return priority;
//    }
}
