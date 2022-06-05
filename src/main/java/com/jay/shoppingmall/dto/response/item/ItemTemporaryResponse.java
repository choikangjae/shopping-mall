package com.jay.shoppingmall.dto.response.item;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemTemporaryResponse {

    private String name;

    private String description;

    private Long price;

    private Long salePrice;

    private Integer stock;
}
