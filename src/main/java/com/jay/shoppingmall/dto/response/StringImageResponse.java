package com.jay.shoppingmall.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StringImageResponse {

    private String mainImage;

    private List<String> descriptionImages;
}
