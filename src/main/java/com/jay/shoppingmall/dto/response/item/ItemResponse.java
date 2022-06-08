package com.jay.shoppingmall.dto.response.item;

import com.jay.shoppingmall.dto.response.ReviewStarCalculationResponse;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemResponse {

    private Long itemId;

    private String mainImage;

    private String option1;

    private String option2;

    private String name;

    private String brandName;

    private Integer zzim;

    private Long originalPrice;

    private Long priceNow;

    private Integer cartQuantity;

    private ReviewStarCalculationResponse reviewStarCalculationResponse;

    @Setter
    private LocalDateTime dateAt;

    @Setter
    private Boolean isZzimed;
}
