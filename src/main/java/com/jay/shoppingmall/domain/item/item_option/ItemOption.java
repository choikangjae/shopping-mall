package com.jay.shoppingmall.domain.item.item_option;

import com.jay.shoppingmall.common.BaseTimeEntity;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.item_price.ItemPrice;
import com.jay.shoppingmall.domain.item.item_stock.ItemStock;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Where(clause = "is_deleted = 0")
@SQLDelete(sql = "UPDATE item_option SET is_deleted = 1, deleted_date = NOW() WHERE id = ?")
public class ItemOption extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String option1;

    private String option2;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_stock_id")
    private ItemStock itemStock;

    //todo 아이템 가격과 연관관계.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_price_id")
    private ItemPrice itemPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(columnDefinition = "boolean default 0")
    private final Boolean isDeleted = false;

    private LocalDateTime deletedDate;
}
