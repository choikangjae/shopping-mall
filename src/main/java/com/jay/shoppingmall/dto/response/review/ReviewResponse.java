package com.jay.shoppingmall.dto.response.review;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewResponse {

    private Long itemId;

    @Setter
    private String itemImage;

    private String itemName;

    private String option1;

    private String option2;

    private String userName;

    private Long reviewId;

    private LocalDateTime reviewWrittenDate;

    private String text;

    private Integer star;

    private List<String> reviewImages;
}
