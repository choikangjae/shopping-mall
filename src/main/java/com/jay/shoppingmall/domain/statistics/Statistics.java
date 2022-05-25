package com.jay.shoppingmall.domain.statistics;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Statistics {

    private Integer totalZzim;

    private Long grossAmount;

    private Long netAmount;

    private Integer totalViewCount;

    private Integer totalItemSoldAmount;
}
