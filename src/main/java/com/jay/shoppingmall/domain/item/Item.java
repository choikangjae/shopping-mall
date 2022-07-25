package com.jay.shoppingmall.domain.item;

import com.jay.shoppingmall.common.BaseTimeEntity;
import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.item.item_option.ItemOption;
import com.jay.shoppingmall.domain.order.Order;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.exception.exceptions.ItemNotFoundException;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Where(clause = "is_deleted = 0")
@SQLDelete(sql = "UPDATE item SET is_deleted = 1, deleted_date = NOW() WHERE id = ?")
public class Item extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String brandName;

    private String description;

    @Setter
    private int zzim;

    private Integer viewCount;

    private Double reviewAverageRating;

    private Integer reviewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

    @Column(columnDefinition = "boolean default 0")
    @Builder.Default
    private Boolean isDeleted = false;

    private LocalDateTime deletedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    public Long getSellerId() {
        return seller.getId();
    }

    public void viewCountUp() {
        if (this.getViewCount() == null) {
            this.viewCount = 1;
        } else {
            this.viewCount += 1;
        }
    }
    public void reviewAverageCalculation(double value) {
        if (this.getReviewAverageRating() == null) {
            this.reviewAverageRating = value;
        } else {
            double totalReviewPoint = this.reviewAverageRating * this.reviewCount;
            this.reviewAverageRating = (totalReviewPoint + value) / (this.reviewCount + 1);
        }

        if (this.getReviewCount() == null) {
            this.reviewCount = 1;
        } else {
            this.reviewCount += 1;
        }
    }

    @Builder
    public Item(Long id, String name, String brandName, String description, Seller seller) {
        this.id = id;
        this.name = name;
        this.brandName = brandName;
        this.description = description;
        this.seller = seller;
    }
}
