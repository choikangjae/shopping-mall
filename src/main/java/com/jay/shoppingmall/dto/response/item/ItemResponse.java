package com.jay.shoppingmall.dto.response.item;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemResponse {

    private Long id;

    private String mainImage;

    private String name;

    private Integer zzim;

    private Integer price;

    private Integer salePrice;

    private Integer cartQuantity;

    private Boolean isZzimed;
}
