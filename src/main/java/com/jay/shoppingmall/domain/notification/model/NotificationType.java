package com.jay.shoppingmall.domain.notification.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationType {
    QNA_TO_SELLER("QnA 등록"),
    QNA_ANSWER_TO_USER("QnA 답변 등록"),
    REVIEW_TO_SELLER("리뷰 등록"),
    ;

    private final String value;
}
