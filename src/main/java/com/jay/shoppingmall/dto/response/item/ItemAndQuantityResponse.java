package com.jay.shoppingmall.dto.response.item;


import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemAndQuantityResponse {

    private Long itemId;

    private String image;

    private String name;

    private Long optionId;

    private String option1;

    private String option2;

    private Integer zzim;

    private Long price;

    private Long salePrice;

    private Integer quantity;
}
