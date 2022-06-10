package com.jay.shoppingmall.dto.response.order;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemResponse {

    private Long orderItemId;

    private String sellerCompanyName;

    private String mainImage;

    private LocalDateTime orderDate;

    private String itemName;

    private Integer quantity;

    private Long itemPrice;

    private String deliveryStatus;

    private Boolean isTrackingStarted;

    private String trackingNumber;
}
