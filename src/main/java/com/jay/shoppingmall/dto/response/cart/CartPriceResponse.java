package com.jay.shoppingmall.dto.response.cart;

import lombok.*;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CartPriceResponse {

    private Long cartManipulatedPrice;
    private CartPricePerSellerResponse cartPricePerSellerResponse;
    private CartPriceTotalResponse cartPriceTotalResponse;

    private List<CartPricePerSellerResponse> cartPricePerSellerResponses;
}
