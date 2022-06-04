package com.jay.shoppingmall.domain.item.item_price;

import com.jay.shoppingmall.domain.item.item_price.model.ItemSale;
import com.jay.shoppingmall.domain.item.item_price.price_history.PriceHistory;
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

    private Boolean isOnSale;

    @Embedded
    private ItemSale itemSale;

    @OneToMany(mappedBy = "itemPrice")
    private List<PriceHistory> priceHistories = new ArrayList<>();
}
