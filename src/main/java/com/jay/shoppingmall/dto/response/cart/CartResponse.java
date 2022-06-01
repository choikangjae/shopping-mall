package com.jay.shoppingmall.dto.response.cart;

import com.jay.shoppingmall.domain.item.Item;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CartResponse {

    private Long id;

    private Integer quantity;

    private Integer multipliedPrice;

    private Integer totalPrice;

    private Integer totalQuantity;

}
