package com.jay.shoppingmall.dto.response.item;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemTemporaryResponse {

    private String brandName;

    private String name;

    private String description;

    private Long originalPrice;

    private Long salePrice;

    private Integer stock;

    private String option1;

    private String option2;

    private Boolean isOptionMainItem;

}
