package com.jay.shoppingmall.dto.response;

import com.jay.shoppingmall.domain.item.Item;
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
