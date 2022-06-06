package com.jay.shoppingmall.dto.request;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemOptionRequest {

    private Long itemId;

    private String option1;

    private String option2;
}
