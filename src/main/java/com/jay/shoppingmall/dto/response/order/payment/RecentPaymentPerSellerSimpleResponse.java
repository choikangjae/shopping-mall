package com.jay.shoppingmall.dto.response.order.payment;

import com.jay.shoppingmall.domain.payment.model.PayMethod;
import com.jay.shoppingmall.domain.payment.model.Pg;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecentPaymentPerSellerSimpleResponse {

    private Long orderId;

    private String pg;

    private String payMethod;

    private String merchantUid;

    private String name;

    private String mainImage;

    private LocalDateTime orderDate;
}
