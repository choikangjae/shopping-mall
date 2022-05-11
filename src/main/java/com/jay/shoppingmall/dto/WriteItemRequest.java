package com.jay.shoppingmall.dto;

import com.jay.shoppingmall.domain.item.Image;
import com.jay.shoppingmall.domain.item.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@Builder
public class WriteItemRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private Image image;

    @NotNull
    private Integer price;

    @NotNull
    private Integer stock;

    public Item toEntity() {
        return Item.builder()
                .name(name)
                .description(description)
                .image(image)
                .price(price)
                .stock(stock)
                .build();
    }
}
