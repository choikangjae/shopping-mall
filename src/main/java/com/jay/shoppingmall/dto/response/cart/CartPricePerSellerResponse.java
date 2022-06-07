package com.jay.shoppingmall.dto.response.cart;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CartPricePerSellerResponse {

    private Long sellerId;
    private Long itemTotalPricePerSeller;
    private Integer itemTotalQuantityPerSeller;
    private Integer itemShippingFeePerSeller;
}
