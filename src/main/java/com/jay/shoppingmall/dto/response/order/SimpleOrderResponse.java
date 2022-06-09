package com.jay.shoppingmall.dto.response.order;

import com.jay.shoppingmall.domain.order.DeliveryStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SimpleOrderResponse {

    private Long id;

    private LocalDateTime orderDate;

    private String name;

    private String mainImage;

    private String deliveryStatus;

    private String merchantUid;

    private Long amount;
}
