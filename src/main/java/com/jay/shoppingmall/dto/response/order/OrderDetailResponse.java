package com.jay.shoppingmall.dto.response.order;

import com.jay.shoppingmall.dto.response.order.payment.PaymentDetailResponse;
import com.jay.shoppingmall.dto.response.order.payment.PaymentPerSellerResponse;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderDetailResponse {

    private List<OrderItemResponse> orderItemResponses;

    private PaymentDetailResponse paymentDetailResponse;

    private List<PaymentPerSellerResponse> paymentPerSellerResponses;
    private PaymentPerSellerResponse paymentPerSellerResponse;
}
