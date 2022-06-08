package com.jay.shoppingmall.dto.response.seller;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecentOrdersForSellerResponse {

    //상품 옵션과 개수, 결제 정보, 받을 사람 주소
    private Long orderId;

    private Long itemOrderPriceAtPurchaseTotal;




}
