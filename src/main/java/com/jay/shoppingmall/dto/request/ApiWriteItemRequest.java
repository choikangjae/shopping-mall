package com.jay.shoppingmall.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ApiWriteItemRequest {

    @NotBlank
    @Size(max = 30, message = "상품명은 30글자 이내로 적어주세요")
    private String itemName;

    @NotBlank
    @Size(max = 30, message = "브랜드명은 30글자 이내로 적어주세요")
    private String itemBrandName;

    @NotBlank
    @Size(max = 200, message = "설명은 200글자 이내로 적어주세요")
    private String description;

    @Size(max = 5, message = "옵션은 최대 5개까지 등록할 수 있습니다")
    private List<Object> optionArray;
//
//    private List<MultipartFile> image;
//
//    @NotNull
//    private MultipartFile mainImage;
}
