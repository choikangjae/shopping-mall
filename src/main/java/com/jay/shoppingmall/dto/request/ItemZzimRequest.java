package com.jay.shoppingmall.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemZzimRequest {

//    @NotNull
//    private Boolean isZzimed;

    @NotNull
    private Long itemId;
}
