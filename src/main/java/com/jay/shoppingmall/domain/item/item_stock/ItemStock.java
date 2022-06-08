package com.jay.shoppingmall.domain.item.item_stock;

import com.jay.shoppingmall.domain.item.item_stock.item_stock_history.ItemStockHistory;
import com.jay.shoppingmall.exception.exceptions.ItemNotFoundException;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemStock {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer stock;

    public Integer stockMinusQuantity(Integer quantity) {
        if (quantity > stock) {
            throw new ItemNotFoundException("주문량이 재고보다 많습니다");
        }
        this.stock -= quantity;
        return stock;
    }
}
