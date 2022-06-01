package com.jay.shoppingmall.dto.request;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentResultValidationRequest {

    private String imp_uid;

    private String merchant_uid;
}
