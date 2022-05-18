package com.jay.shoppingmall.domain.image;


import com.jay.shoppingmall.domain.item.Item;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 랜덤 번호 + 원본 이미지, 썸네일 이미지, 유동적인 경로
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String originalFileName;

    private String filePath;

    private Long fileSize;

    private boolean isMainImage;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Builder
    public Image(String originalFileName, String filePath, Long fileSize, boolean isMainImage) {
        this.originalFileName = originalFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.isMainImage = isMainImage;
    }

    //상품 정보 저장
    public void setItem(Item item) {
        this.item = item;

        //게시글에 현재 파일이 존재하지 않으면 파일 추가.
        if (!item.getImageList().contains(this))
            item.getImageList().add(this);
    }

}
