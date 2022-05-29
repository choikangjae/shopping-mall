package com.jay.shoppingmall.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SellerAgreeRequest {

    private Boolean isSellerAgree;

    private Boolean isLawAgree;
}
