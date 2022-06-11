package com.jay.shoppingmall.domain.point.point_history.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointType {

    REVIEW("리뷰 작성"),
    PURCHASE("상품 구매"),
    ;
    private final String type;
}
