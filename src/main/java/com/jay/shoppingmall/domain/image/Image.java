package com.jay.shoppingmall.domain.image;


import com.jay.shoppingmall.common.BaseTimeEntity;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.review.Review;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "is_deleted = 0")
@SQLDelete(sql = "UPDATE Image SET is_deleted = 1, deleted_date = NOW() WHERE id = ?")
public class Image extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    private Long itemId;

    private String originalFileName;

    private String filePath;

    private Long fileSize;

    @Setter
    private Boolean isMainImage;

    @Column(columnDefinition = "boolean default 0")
    private Boolean isDeleted = false;

    private LocalDateTime deletedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "review_id")
//    private Review review;

    @Builder
    public Image(String originalFileName, String filePath, Long fileSize, boolean isMainImage, Item item) {
        this.originalFileName = originalFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.isMainImage = isMainImage;
        this.item = item;
    }

    //상품 정보 저장
//    public void setItem(Item item) {
//        this.item = item;

        //게시글에 현재 파일이 존재하지 않으면 파일 추가.
//        if (!item.getImageList().contains(this))
//            item.getImageList().add(this);
//    }

}
