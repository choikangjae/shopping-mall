package com.jay.shoppingmall.domain.point.point_history.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointStatus {
    PENDING("지급 대기"),
    PAID("지급 완료"),
    CANCELLED("구매 취소"),
    USED("사용")
    ;
    private final String status;
}
