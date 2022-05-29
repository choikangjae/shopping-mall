package com.jay.shoppingmall.dto.response.item;


import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemAndQuantityResponse {

    private Long id;

    private String image;

    private String name;

    private Integer zzim;

    private Integer price;

    private Integer salePrice;

    private Integer quantity;
}
