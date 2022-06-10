package com.jay.shoppingmall.dto.response;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class VirtualDeliveryResponseForAnonymous {

    private String trackingNumber;

    private String senderName;

    private String receiverName;
}
