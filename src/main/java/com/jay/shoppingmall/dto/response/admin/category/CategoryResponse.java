package com.jay.shoppingmall.dto.response.admin.category;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryResponse {

    private String parentCategory;

    private String childCategory;
}
