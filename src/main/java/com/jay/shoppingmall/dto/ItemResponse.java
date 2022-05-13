package com.jay.shoppingmall.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemResponse {

    private Long id;

    private String name;

    private Integer price;

    private String image;
}
