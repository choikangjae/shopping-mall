package com.jay.shoppingmall.dto.response.item;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemSimpleResponse {

    private Long itemId;

    private String itemName;

    private String itemImage;
}
