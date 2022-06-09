package com.jay.shoppingmall.domain.payment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayMethod {
    CARD("카드 결제"),
    TRANS("실시간 계좌 이체"),
    PHONE("핸드폰 결제"),
    ;
    private final String name;
}
