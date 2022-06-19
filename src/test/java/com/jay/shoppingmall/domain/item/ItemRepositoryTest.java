package com.jay.shoppingmall.domain.item;

import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    SellerRepository sellerRepository;

    Item item;
    Item item2;
    Item item3;

    Seller seller;
    Seller seller2;


    Pageable pageable;

    @BeforeEach
    void setUp() {
        seller = Seller.builder()
                .id(1L)
                .build();
        seller2 = Seller.builder()
                .id(2L)
                .build();
        sellerRepository.save(seller);
        sellerRepository.save(seller2);

        item = Item.builder()
                .name("상품명")
                .seller(seller)
                .build();
        item2 = Item.builder()
                .name("상품명2")
                .seller(seller)
                .build();
        item3 = Item.builder()
                .name("명품상")
                .seller(seller2)
                .build();

        itemRepository.save(item);
        itemRepository.save(item2);
        itemRepository.save(item3);
        pageable = PageRequest.of(0, 5);
    }

    @Test
    void findByNameContaining() {
        final Page<Item> items = itemRepository.findByNameContaining("상품명", Pageable.unpaged());

        assertThat(items.getContent().size()).isEqualTo(2);
        assertThat(items).doesNotContain(item3);
    }

    @Test
    void findBySellerId() {
        final Page<Item> items = itemRepository.findBySellerId(1L, Pageable.unpaged());

        assertThat(items).doesNotContain(item3);
        assertThat(items).contains(item);
    }

    @Test
    void findFirst3BySellerId() {
        Item moreItem1 = Item.builder()
                .name("상품명")
                .seller(seller)
                .build();
        Item moreItem2 = Item.builder()
                .name("상품명2")
                .seller(seller)
                .build();
        
        itemRepository.save(moreItem1);
        itemRepository.save(moreItem2);

        final List<Item> items = itemRepository.findFirst3BySellerId(1L);
        final List<Item> itemAll = itemRepository.findAllBySellerId(1L);

        assertThat(items.size()).isEqualTo(3);
        assertThat(itemAll.size()).isGreaterThan(3);
    }
}