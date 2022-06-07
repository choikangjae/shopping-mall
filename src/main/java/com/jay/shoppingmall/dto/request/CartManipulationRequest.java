package com.jay.shoppingmall.dto.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartManipulationRequest {

    @NotNull
    private Long cartId;

    private Integer cartQuantity;

    private Boolean isSelected;
}
