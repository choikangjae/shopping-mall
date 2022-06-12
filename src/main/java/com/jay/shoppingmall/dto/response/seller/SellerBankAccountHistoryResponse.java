package com.jay.shoppingmall.dto.response.seller;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SellerBankAccountHistoryResponse {

    private Long id;

    private Long transactionMoney;
}
