package com.jay.shoppingmall.domain.item.item_price.price_history;

import com.jay.shoppingmall.domain.item.item_option.ItemOption;
import com.jay.shoppingmall.domain.item.item_price.ItemPrice;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//가격이 변동될 때마다 무조건 새로 생성.
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long price;

    private LocalDateTime priceUpdateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_price_id")
    private ItemPrice itemPrice;
}
