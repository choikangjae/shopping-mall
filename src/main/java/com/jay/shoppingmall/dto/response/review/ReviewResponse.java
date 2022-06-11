package com.jay.shoppingmall.dto.response.review;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewResponse {

    private Long reviewId;

    private String text;

    private Integer star;

    private List<String> reviewImages;
}
