package com.jay.shoppingmall.domain.cart;

import com.jay.shoppingmall.common.BaseTimeEntity;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.exception.exceptions.QuantityException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Cart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Cart(final Integer quantity, final Item item, final User user) {
        this.quantity = quantity;
        this.item = item;
        this.user = user;
    }

    public void manipulateQuantity(Integer quantity) {
        if (quantity < 1 || quantity > item.getStock()) {
            throw new QuantityException("주문할 수 있는 재고 "+ item.getStock() +"개를 넘어섰습니다.");
        }
        this.quantity = quantity;
    }
}
