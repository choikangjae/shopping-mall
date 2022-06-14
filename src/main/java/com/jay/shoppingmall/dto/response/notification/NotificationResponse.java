package com.jay.shoppingmall.dto.response.notification;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationResponse {

    private Long notificationId;

    private String notificationType;

    private String message;

    private Boolean isRead;

    private LocalDateTime readAt;

    private LocalDateTime sentAt;

    public void setRead(LocalDateTime readAt) {
        this.isRead = true;
        this.readAt = readAt;
    }
}
