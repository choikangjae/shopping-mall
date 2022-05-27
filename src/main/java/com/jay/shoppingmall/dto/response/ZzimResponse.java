package com.jay.shoppingmall.dto.response;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
public class ZzimResponse {
    private Integer zzimPerItem;

    private Boolean isZzimed;
}
