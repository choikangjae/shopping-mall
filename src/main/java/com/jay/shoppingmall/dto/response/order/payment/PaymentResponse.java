package com.jay.shoppingmall.dto.response.order.payment;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentResponse {

    private Long amount;

    private String merchantUid;
}
