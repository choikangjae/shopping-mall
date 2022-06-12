package com.jay.shoppingmall.dto.response.item;

import com.jay.shoppingmall.dto.response.review.ReviewStarCalculationResponse;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ItemDetailResponse {

    private Long id;

    private String name;

    private String brandName;

    private Map<String,List<String>> optionMap;

    private String description;

    private String mainImage;

    private List<String> descriptionImages;

    private Long originalPrice;

    private Long priceNow;

    private ReviewStarCalculationResponse reviewStarCalculationResponse;

    private Integer stock;

    private Integer zzim;

    private Boolean isZzimed;

    private Boolean isSellerItem;
}
