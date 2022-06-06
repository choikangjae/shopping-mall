package com.jay.shoppingmall.dto.response.item;

import com.jay.shoppingmall.dto.response.ReviewStarCalculationResponse;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemResponse {

    private Long id;

    private String mainImage;

    private String name;

//    private String brandName;

    private Integer zzim;

    private Long originalPrice;

    private Long priceNow;

    private Integer cartQuantity;

    private ReviewStarCalculationResponse reviewStarCalculationResponse;

    private Boolean isZzimed;
}
