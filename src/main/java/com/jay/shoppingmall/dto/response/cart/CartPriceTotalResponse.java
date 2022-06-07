package com.jay.shoppingmall.dto.response.cart;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CartPriceTotalResponse {

    private Long cartTotalPrice;
    private Integer cartTotalQuantity;
    private Integer cartTotalShippingFee;
}
