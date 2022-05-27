package com.jay.shoppingmall.domain.qna;

public enum QnaCategory {

    ABOUT_ITEM("상품문의"),
    ABOUT_RESTOCK("재입고문의"),
    ABOUT_SIZE("사이즈문의"),
    ABOUT_DELIVERY("배송문의"),
    ABOUT_OTHERS("기타"),
    ;

    private final String value;

    QnaCategory(String value) {
        this.value = value;
    }
    public String value() {
        return value;
    }
}
