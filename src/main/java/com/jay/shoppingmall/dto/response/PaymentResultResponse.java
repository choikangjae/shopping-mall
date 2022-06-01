package com.jay.shoppingmall.dto.response;

import com.jay.shoppingmall.domain.payment.PayMethod;
import com.jay.shoppingmall.domain.payment.Pg;
import lombok.*;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentResultResponse {

    private Long id;

    private Long userId;

    private Pg pg;

    private PayMethod payMethod;

    private String merchantUid;

    private String name;

    private Long amount;

    private String buyerEmail;

    private String buyerName;

    private String buyerTel;

    private String buyerAddr;

    private String buyerPostcode;

}
