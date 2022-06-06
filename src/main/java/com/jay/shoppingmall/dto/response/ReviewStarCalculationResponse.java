package com.jay.shoppingmall.dto.response;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewStarCalculationResponse {

    private Double reviewAverageRating;

    private Double fullStar;

    private Double halfStar;

    private Double emptyStar;

}
