package com.jay.shoppingmall.dto;

import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.item.Item;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class WriteItemRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private List<MultipartFile> image;

    private Image mainImage;

    @NotNull
    private Integer price;

    private Integer salePrice;

    @NotNull
    private Integer stock;

    public Item toEntity() {
        return Item.builder()
                .name(name)
                .description(description)
                .price(price)
                .salePrice(salePrice)
                .stock(stock)
                .build();
    }
}
