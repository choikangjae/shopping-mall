package com.jay.shoppingmall.domain.item.item_price.price_history;

import com.jay.shoppingmall.domain.item.item_price.ItemPrice;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemPriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long price;

    private LocalDateTime priceUpdateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_price_id")
    private ItemPrice itemPrice;
}
