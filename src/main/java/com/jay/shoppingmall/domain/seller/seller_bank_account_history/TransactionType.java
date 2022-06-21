package com.jay.shoppingmall.domain.seller.seller_bank_account_history;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionType {
    DEPOSIT("입금"),
    WITHDRAWAL("출금"),
    ;
    private final String value;
}
