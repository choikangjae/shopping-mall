package com.jay.shoppingmall.dto.response.order.payment;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecentPaymentPerSellerResponse {

    private RecentPaymentPerSellerSimpleResponse recentPaymentPerSellerSimpleResponse;

    private PaymentPerSellerResponse paymentPerSellerResponse;
}
