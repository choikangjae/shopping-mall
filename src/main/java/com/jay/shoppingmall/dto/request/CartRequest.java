package com.jay.shoppingmall.dto.request;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartRequest {

    private Long itemId;

    private Integer quantity;

    private Long optionId;

    private String option1;

    private String option2;

    private Integer totalPrice;

    private Integer totalQuantity;
//    @Setter
//    private String totalPrice;
}
