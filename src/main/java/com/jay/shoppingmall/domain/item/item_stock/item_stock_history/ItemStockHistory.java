package com.jay.shoppingmall.domain.item.item_stock.item_stock_history;

import com.jay.shoppingmall.domain.item.item_price.ItemPrice;
import com.jay.shoppingmall.domain.item.item_stock.ItemStock;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemStockHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer stock;

    private LocalDateTime stockChangedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_stock_id")
    private ItemStock itemStock;
}
