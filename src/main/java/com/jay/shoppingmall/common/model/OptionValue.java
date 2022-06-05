package com.jay.shoppingmall.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class OptionValue {

    @JsonIgnore
    private String identifier;

    private Boolean isOptionMainItem;

    private String option1;

    private String option2;

    private Long optionSalePrice;

    private Long optionOriginalPrice;

    private Integer optionStock;
}
