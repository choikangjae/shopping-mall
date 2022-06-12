package com.jay.shoppingmall.dto.response.seller;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SellerBankResponse {

    private Long sellerId;

    private Long bankAccount;

    private List<SellerBankAccountHistoryResponse> sellerBankAccountHistoryResponses;
}
