package com.jay.shoppingmall.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ItemDetailResponse {

    private Long id;

    private String name;

    private String description;

    private String image;

    private Integer price;

    private Integer stock;

    private Integer zzim;
}
