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
import com.jay.shoppingmall.domain.zzim.Zzim;
import com.jay.shoppingmall.domain.zzim.ZzimRepository;
import com.jay.shoppingmall.exception.exceptions.ItemNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
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
    @Autowired
    ZzimRepository zzimRepository;
    Item item;
    ItemPrice itemPrice;
    User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
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

        Zzim zzim = Zzim.builder()
                .isZzimed(false)
                .item(item)
                .user(user)
                .build();
        zzimRepository.save(zzim);
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

    @Test
    void deleteByUserIdAndItemId() {
        int numberOfDeleted = zzimRepository.deleteByUserIdAndItemId(user.getId(), item.getId());

        assertThat(numberOfDeleted).isEqualTo(1);
    }

    @Test
    void findByItemIds() {
        List<Long> itemIds = new ArrayList<>();
        for (long i = 0; i < 1000; i++) {

            Item item = Item.builder()
                    .name("상품명" + i)
                    .build();
            itemRepository.save(item);

            Zzim zzim = Zzim.builder()
                    .item(item)
                    .build();
            zzimRepository.save(zzim);
            itemIds.add(item.getId());
        }

        final List<Zzim> zzims = zzimRepository.findByItemIdIn(itemIds);
        assertThat(zzims.size()).isEqualTo(10);
    }
}