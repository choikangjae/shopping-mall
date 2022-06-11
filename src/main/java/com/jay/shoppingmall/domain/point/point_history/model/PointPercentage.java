package com.jay.shoppingmall.domain.point.point_history.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointPercentage {

    ONLY_TEXT(0.005),
    TEXT_AND_PICTURE(0.01),
    ;

    private final Double percentage;

}
