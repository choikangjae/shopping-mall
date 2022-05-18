package com.jay.shoppingmall.dto;

import com.jay.shoppingmall.domain.image.Image;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageUpload {

    private String originalFileName;

    private String filePath;

    private Long fileSize;

    private boolean isMainImage;

    public Image toEntity() {
        return Image.builder()
                .originalFileName(originalFileName)
                .filePath(filePath)
                .fileSize(fileSize)
                .isMainImage(isMainImage)
                .build();
    }
}
