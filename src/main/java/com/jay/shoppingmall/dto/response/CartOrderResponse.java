package com.jay.shoppingmall.dto.response;

import com.jay.shoppingmall.domain.item.Item;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
public class CartOrderResponse {

    private Integer quantity;

    private Item item;
}
