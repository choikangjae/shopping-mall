package com.jay.shoppingmall.dto.response;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentResponse {

    private Long amount;

    private String merchantUid;
}
