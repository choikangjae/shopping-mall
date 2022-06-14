package com.jay.shoppingmall.dto.response.notification;

import com.jay.shoppingmall.dto.response.item.ItemSimpleResponse;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeNotificationResponse {

    private NotificationResponse notificationResponse;

    private ItemSimpleResponse itemSimpleResponse;

    private String originalMessage;
}
