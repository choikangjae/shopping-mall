package com.jay.shoppingmall.dto.response.item;

import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemOptionResponse {

    private Long itemId;

    private Long itemOptionId;

    private String option1;

    private String option2;

    private Integer itemQuantity;

    private Long itemPrice;

    private Integer itemStock;

    private Long itemTotalPrice;

    private Integer itemTotalQuantity;
}
