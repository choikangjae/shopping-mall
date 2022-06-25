package com.jay.shoppingmall.domain.item;

import com.jay.shoppingmall.domain.seller.Seller;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    Seller seller = Seller.builder()
            .id(0L)
            .build();

    Item item = Item.builder()
            .seller(seller)
            .build();


    @Test
    void getSellerId() {
        final Long sellerId = item.getSellerId();

        assertThat(sellerId).isEqualTo(seller.getId());
    }
}