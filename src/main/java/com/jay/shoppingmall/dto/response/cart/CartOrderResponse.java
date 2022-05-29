package com.jay.shoppingmall.dto.response.cart;

import com.jay.shoppingmall.dto.response.item.ItemResponse;
import lombok.*;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
public class CartOrderResponse {

    private Integer orderTotalPrice;

    private Integer orderTotalCount;

    private List<ItemResponse> itemResponses;

}
