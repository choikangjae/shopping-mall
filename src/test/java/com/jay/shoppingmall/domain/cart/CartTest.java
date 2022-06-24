package com.jay.shoppingmall.domain.cart;

import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.item.item_option.ItemOption;
import com.jay.shoppingmall.domain.item.item_stock.ItemStock;
import com.jay.shoppingmall.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    User user = EntityBuilder.getUser();
    ItemOption itemOption = EntityBuilder.getItemOption();

    Cart cart = Cart.builder()
            .itemOption(itemOption)
            .build();

    @Test
    void manipulateQuantity() {
        final Integer quantity = cart.manipulateQuantity(5);

        assertThat(quantity).isEqualTo(5);
    }

    @Test
    void getPriceNow() {
        final Long priceNow = cart.getPriceNow();

        assertThat(priceNow).isEqualTo(cart.getItemOption().getItemPrice().getPriceNow());
        assertThat(priceNow).isEqualTo(cart.getItemOption().getPriceNow());
    }
}