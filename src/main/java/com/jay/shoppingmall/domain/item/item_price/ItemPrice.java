package com.jay.shoppingmall.domain.item.item_price;

import com.jay.shoppingmall.domain.item.item_price.model.ItemOnSale;
import com.jay.shoppingmall.domain.item.item_price.price_history.ItemPriceHistory;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Where(clause = "is_deleted = 0")
//@SQLDelete(sql = "UPDATE item_price SET is_deleted = 1, deleted_date = NOW() WHERE id = ?")
public class ItemPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long originalPrice;

    private Long priceNow;

    @Column(columnDefinition = "boolean default 0")
    private Boolean isOnSale = false;

    @Embedded
    private ItemOnSale itemOnSale;

    @OneToMany(mappedBy = "itemPrice")
    private List<ItemPriceHistory> priceHistories = new ArrayList<>();
}
