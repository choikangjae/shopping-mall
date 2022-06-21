package com.jay.shoppingmall.domain.item.temporary;

import com.jay.shoppingmall.common.BaseTimeEntity;
import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.order.Order;
import com.jay.shoppingmall.domain.seller.Seller;
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
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemTemporary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String brandName;

    private String name;

    private String description;

    private Long originalPrice;

    private Long salePrice;

    private Integer stock;

    private String option1;

    private String option2;

    private Boolean isOptionMainItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;
}
