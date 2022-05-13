package com.jay.shoppingmall.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ItemDetailResponse {

    private String name;

    private String description;

    private String image;

    private Integer price;

    private Integer stock;
}
