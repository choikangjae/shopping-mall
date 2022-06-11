package com.jay.shoppingmall.dto.request;

import com.jay.shoppingmall.domain.review.Star;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewWriteRequest {

    @NotNull
    private Long orderItemId;

    @NotNull
    private Integer star;

    @NotBlank
    private String text;
}
