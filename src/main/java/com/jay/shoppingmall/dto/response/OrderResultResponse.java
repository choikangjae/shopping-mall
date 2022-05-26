package com.jay.shoppingmall.dto.response;

import com.jay.shoppingmall.domain.payment.PaymentType;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResultResponse {

    private List<ItemResponse> itemResponseList;

    private Integer totalPrice;
    private CartOrderResponse cartOrderResponse;

    private PaymentType paymentType;

    private Boolean isShippingFeeFree;
}
