package com.jay.shoppingmall.dto.response.order.payment;

import com.jay.shoppingmall.domain.payment.Payment;
import com.jay.shoppingmall.domain.payment.model.PayMethod;
import com.jay.shoppingmall.domain.payment.model.Pg;
import lombok.*;

import javax.validation.constraints.NotEmpty;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentDetailResponse {

    private Long id;

    private Long userId;

    private String pg;

    private String payMethod;

    private String merchantUid;

    private String name;

    private Long amount;

    private Integer paymentTotalShippingFee;

    private Long paymentTotalPrice;

    private String buyerEmail;

    private String buyerName;

    private String buyerTel;

    private String buyerAddr;

    private String buyerPostcode;

    private String receiverAddress;

    private String receiverEmail;

    private String receiverName;

    private String receiverPostcode;

    private String receiverPhoneNumber;

    private Boolean isAllItemTrackingNumberIssued;

}
