package com.jay.shoppingmall.dto.response.order.payment;

import com.jay.shoppingmall.domain.payment.payment_per_seller.PaymentPerSeller;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentPerSellerResponse {

    private Long paymentPerSellerId;

    private Long itemTotalPricePerSeller;

    private Integer itemTotalQuantityPerSeller;

    private Integer itemShippingFeePerSeller;

}
