package com.jay.shoppingmall.domain.review;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

public enum Star {
//    TEN(10),
//    NINE(9),
//    EIGHT(8),
//    SEVEN(7),
//    SIX(6),
    FIVE(5),
    FOUR(4),
    THREE(3),
    TWO(2),
    ONE(1),
    ;
    private final Integer value;

    Star(Integer value) {
        this.value = value;
    }
    public Integer value() {
        return value;
    }
    public static Star getByValue(Integer value) {
        return Arrays.stream(values())
                .filter(v -> Objects.equals(v.value, value))
                .findAny()
                .orElse(null);
    }
}
