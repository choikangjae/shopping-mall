package com.jay.shoppingmall.dto.response;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemResponse {

    private Long id;

    private String image;

    private String name;

    private Integer zzim;

    private Integer price;

    private Integer salePrice;
}
