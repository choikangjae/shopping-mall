package com.jay.shoppingmall.dto.response.review;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemForReviewResponse {

    private Long orderItemId;
    private String name;
    private Integer pointOnlyText;
    private Integer pointWithPicture;
}
