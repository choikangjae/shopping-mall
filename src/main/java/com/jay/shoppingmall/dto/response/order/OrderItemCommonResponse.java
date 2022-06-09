package com.jay.shoppingmall.dto.response.order;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemCommonResponse {

    private String sellerCompanyName;

    private LocalDateTime orderDate;

    private Integer shippingFee;

    private Long totalPricePerSeller;

    private Integer totalQuantityPerSeller;
}
