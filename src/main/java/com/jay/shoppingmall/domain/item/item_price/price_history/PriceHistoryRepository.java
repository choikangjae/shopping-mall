package com.jay.shoppingmall.domain.item.item_price.price_history;

import com.jay.shoppingmall.domain.item.item_price.ItemPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

}
