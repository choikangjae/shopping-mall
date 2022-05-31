package com.jay.shoppingmall.dto.request;

import com.jay.shoppingmall.domain.payment.PayMethod;
import com.jay.shoppingmall.domain.payment.Pg;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentRequest {

    private Pg pg;

    private PayMethod payMethod;

//    private String merchantUid;

//    private String name;

//    private Long amount;

    private String buyerEmail;

    private String buyerName;

    private String buyerTel;

    private String buyerAddr;

    private String buyerPostcode;
}
