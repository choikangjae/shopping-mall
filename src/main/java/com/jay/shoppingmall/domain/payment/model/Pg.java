package com.jay.shoppingmall.domain.payment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Pg {
    HTML5_INICIS("이니시스"),
    KAKAOPAY("카카오페이"),
    TOSSPAY("토스페이"),
    MUTONGJANG("무통장 입금");

    private final String name;
}
