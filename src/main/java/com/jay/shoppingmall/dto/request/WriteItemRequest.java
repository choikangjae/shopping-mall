package com.jay.shoppingmall.dto.request;

import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.item.Item;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class WriteItemRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

//    private Map<String, List<String>> options;
    //옵션은 테이블을 따로 만들어서 조인시키는 것이 좋아보임
    private String option1;

    private String option2;

    private List<MultipartFile> image;

    @NotNull
    private MultipartFile mainImage;

    @NotNull
    private Integer price;

    private Integer salePrice;

    @NotNull
    private Integer stock;
}
