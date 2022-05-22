package com.jay.shoppingmall.domain.review;

public enum Star {
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
}
