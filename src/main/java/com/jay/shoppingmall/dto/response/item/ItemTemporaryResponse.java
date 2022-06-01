package com.jay.shoppingmall.dto.response.item;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemTemporaryResponse {

    private String name;

    private String description;

    private Integer price;

    private Integer salePrice;

    private Integer stock;
}
