package com.jay.shoppingmall.dto.response;

import com.jay.shoppingmall.domain.item.Item;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
public class CartResponse {

    private Long id;

    private Integer quantity;

    private Integer multipliedPrice;

    private Integer totalPrice;

    private Integer totalQuantity;

}
