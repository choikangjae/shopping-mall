package com.jay.shoppingmall.dto.request.admin.category;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryAddRequest {

    private String parentCategory;
    @NotBlank
    private String childCategory;
}
