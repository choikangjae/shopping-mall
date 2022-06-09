package com.jay.shoppingmall.dto.request;

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
    private String itemName;

    @NotBlank
    private String description;

    @NotBlank
    private String itemBrandName;

    private List<MultipartFile> image;

    private MultipartFile mainImage;

    private Long originalPrice;

    @NotNull
    private Long salePrice;

    @NotNull
    private Integer stock;
}
