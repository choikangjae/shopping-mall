package com.jay.shoppingmall.dto.request;

import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.payment.PaymentType;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentRequest {

    @NotNull
    private Long itemId;

    @NotNull
    private Integer itemQuantity;

    private Integer totalPrice;

    private PaymentType paymentType;

    private Boolean isShippingFeeFree;
}
