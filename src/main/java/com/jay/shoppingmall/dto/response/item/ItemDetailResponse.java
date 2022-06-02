package com.jay.shoppingmall.dto.response.item;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ItemDetailResponse {

    private Long id;

    private String name;

    private Map<String,List<String>> optionMap;

    private String description;

    private String mainImage;

    private List<String> descriptionImages;

    private Integer price;

    private Integer stock;

    private Integer zzim;

    private Boolean isZzimed;
}
