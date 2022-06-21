package com.jay.shoppingmall.domain.item.item_option;

import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.item.item_price.ItemPrice;
import com.jay.shoppingmall.domain.item.item_price.ItemPriceRepository;
import com.jay.shoppingmall.domain.item.item_stock.ItemStock;
import com.jay.shoppingmall.domain.item.item_stock.ItemStockRepository;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.domain.user.Role;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.exception.exceptions.ItemNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemOptionRepositoryTest {

    @Autowired
    ItemOptionRepository itemOptionRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemStockRepository itemStockRepository;
    @Autowired
    ItemPriceRepository itemPriceRepository;
    @Autowired
    SellerRepository sellerRepository;
    @Autowired
    UserRepository userRepository;
    Item item;
    ItemPrice itemPrice;
    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .email("qwe@qwe")
                .role(Role.ROLE_USER)
                .password(("qweqweqwe1"))
                .build();
        userRepository.save(user);

        final Seller seller = EntityBuilder.getSeller();
        sellerRepository.save(seller);

        item = EntityBuilder.getItem();
        itemRepository.save(item);

        final ItemStock itemStock = EntityBuilder.getItemStock();
        itemStockRepository.save(itemStock);

        itemPrice = EntityBuilder.getItemPrice();
        itemPriceRepository.save(itemPrice);

        final ItemOption itemOption = EntityBuilder.getItemOption();
        itemOptionRepository.save(itemOption);
    }

    @Test
    void findByItemId() {
        final ItemOption itemOption2 = EntityBuilder.getItemOption2();
        itemOptionRepository.save(itemOption2);

        final List<ItemOption> itemOptions = itemOptionRepository.findByItemId(item.getId());

        assertThat(itemOptions.size()).isEqualTo(2);
    }

    @Test
    void findAllByOption1AndItemId() {
        final ItemOption itemOption2 = EntityBuilder.getItemOption2();
        itemOptionRepository.save(itemOption2);

        final List<ItemOption> itemOptions = itemOptionRepository.findAllByOption1AndItemId("option1", item.getId());

        assertThat(itemOptions.size()).isEqualTo(2);
        assertThat(itemOptions.get(0).getOption1()).isEqualTo("option1");
        assertThat(itemOptions.get(1).getOption1()).isEqualTo("option1");
    }

    @Test
    void findByOption1AndOption2AndItemId() {
        final ItemOption itemOption = itemOptionRepository.findByOption1AndOption2AndItemId("option1", "option2", item.getId())
                        .orElseThrow(() -> new ItemNotFoundException(""));

        assertThat(itemOption.getOption1()).isEqualTo("option1");
        assertThat(itemOption.getOption2()).isEqualTo("option2");
    }

    @Test
    void findByItemIdAndIsOptionMainItemTrue() {
        final ItemOption itemOption = itemOptionRepository.findByItemIdAndIsOptionMainItemTrue(item.getId());

        assertThat(itemOption.getIsOptionMainItem()).isTrue();

    }

    @Test
    void findByItemPriceId() {
        final ItemOption itemOption = itemOptionRepository.findByItemPriceId(itemPrice.getId());

        assertThat(itemOption).isNotNull();
    }
}