package com.jay.shoppingmall.dto.response;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemResponse {

    private Long id;

    private String name;

    private Integer price;

    private String image;
}
