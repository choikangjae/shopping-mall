package com.jay.shoppingmall.domain.cart;

import com.jay.shoppingmall.common.BaseTimeEntity;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.user.User;
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
            throw new ArithmeticException("Something is off about the quantity");
        }
        this.quantity = quantity;
    }
}
