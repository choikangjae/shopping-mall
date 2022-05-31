package com.jay.shoppingmall.dto.response;

import com.jay.shoppingmall.domain.payment.Pg;
import com.jay.shoppingmall.dto.response.cart.CartOrderResponse;
import com.jay.shoppingmall.dto.response.item.ItemResponse;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResultResponse {

    private List<ItemResponse> itemResponseList;

    private Long amount;

    private CartOrderResponse cartOrderResponse;

    private Pg pg;

    private Boolean isShippingFeeFree;
}
