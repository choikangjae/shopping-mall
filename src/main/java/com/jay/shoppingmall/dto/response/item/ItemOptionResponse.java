package com.jay.shoppingmall.dto.response.item;

import lombok.*;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemOptionResponse {

    private String option1;

    private String option2;

    private Integer itemStock;

    private Long itemPrice;
}
