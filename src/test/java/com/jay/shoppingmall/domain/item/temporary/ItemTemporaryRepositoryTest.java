package com.jay.shoppingmall.domain.item.temporary;

import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.item.item_price.ItemPriceRepository;
import com.jay.shoppingmall.domain.item.item_stock.ItemStockRepository;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.domain.user.Role;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.response.item.ItemTemporaryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemTemporaryRepositoryTest {

    @Autowired
    ItemTemporaryRepository itemTemporaryRepository;
    @Autowired
    SellerRepository sellerRepository;
    @Autowired
    UserRepository userRepository;

    Seller seller;
    ItemTemporary itemTemporary1;
    ItemTemporary itemTemporary2;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .email("qwe@qwe")
                .role(Role.ROLE_USER)
                .password(("qweqweqwe1"))
                .build();
        userRepository.save(user);

        seller = EntityBuilder.getSeller();
        sellerRepository.save(seller);

        itemTemporary1 = ItemTemporary.builder()
                .option1("색깔")
                .option2("빨강")
                .isOptionMainItem(true)
                .originalPrice(5000L)
                .salePrice(3000L)
                .stock(5)
                .brandName("나이키")
                .name("상품명")
                .description("상품 설명")
                .seller(seller)
                .build();
        itemTemporary2 = ItemTemporary.builder()
                .option1("색깔")
                .option2("빨강")
                .isOptionMainItem(false)
                .originalPrice(5000L)
                .salePrice(3000L)
                .stock(5)
                .brandName("나이키")
                .name("상품명")
                .description("상품 설명")
                .seller(seller)
                .build();
    }

    @Test
    void findAllBySellerId() {
        itemTemporaryRepository.save(itemTemporary1);
        itemTemporaryRepository.save(itemTemporary2);

        final List<ItemTemporary> itemTemporaries = itemTemporaryRepository.findAllBySellerId(seller.getId());

        assertThat(itemTemporaries.size()).isEqualTo(2);
    }
}