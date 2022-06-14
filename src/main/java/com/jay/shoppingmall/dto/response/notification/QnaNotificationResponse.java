package com.jay.shoppingmall.dto.response.notification;

import com.jay.shoppingmall.dto.response.item.ItemSimpleResponse;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QnaNotificationResponse {

    private NotificationResponse notificationResponse;

    private ItemSimpleResponse itemSimpleResponse;

    private boolean isAnswered;
    private LocalDateTime answeredAt;
    private String answer;

    private String qnaCategory;

    public void setAnswer(LocalDateTime answeredAt, String answer) {
        this.isAnswered = true;
        this.answeredAt = answeredAt;
        this.answer = answer;
    }
}
