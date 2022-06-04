package com.jay.shoppingmall.domain.item.item_stock;

import com.jay.shoppingmall.domain.item.item_price.ItemPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemStockRepository extends JpaRepository<ItemPrice, Long> {

}
