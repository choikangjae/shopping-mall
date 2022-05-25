package com.jay.shoppingmall.dto.request;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartRequest {

    private Long id;

    private Integer quantity;

    private Integer totalPrice;

    private Integer totalQuantity;
//    @Setter
//    private String totalPrice;
}
