package com.jay.shoppingmall.domain.image;

import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.item.item_option.ItemOption;
import com.jay.shoppingmall.domain.item.item_option.ItemOptionRepository;
import com.jay.shoppingmall.domain.item.item_price.ItemPrice;
import com.jay.shoppingmall.domain.item.item_price.ItemPriceRepository;
import com.jay.shoppingmall.domain.item.item_stock.ItemStock;
import com.jay.shoppingmall.domain.item.item_stock.ItemStockRepository;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.domain.user.Role;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.exception.exceptions.ImageNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ImageRepositoryTest {

    @Autowired
    ImageRepository imageRepository;
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

        Image image1 = Image.builder()
                .originalFileName("메인 이미지")
                .imageRelation(ImageRelation.ITEM_MAIN)
                .foreignId(0L)
                .build();
        Image image2 = Image.builder()
                .originalFileName("설명 이미지1")
                .imageRelation(ImageRelation.ITEM_DESCRIPTION)
                .foreignId(0L)
                .build();
        Image image3 = Image.builder()
                .originalFileName("메인 이미지2")
                .imageRelation(ImageRelation.ITEM_DESCRIPTION)
                .foreignId(0L)
                .build();
        imageRepository.save(image1);
        imageRepository.save(image2);
        imageRepository.save(image3);
    }

    @Test
    void findAllByForeignId() {
        final List<Image> images = imageRepository.findAllByForeignId(0L);

        final Image mainImage = images.stream().filter(x -> x.getImageRelation().equals(ImageRelation.ITEM_MAIN)).findFirst()
                        .orElseThrow(() -> new ImageNotFoundException(""));
        final List<Image> descriptionImages = images.stream().filter(x -> x.getImageRelation().equals(ImageRelation.ITEM_DESCRIPTION)).collect(Collectors.toList());

        assertThat(mainImage).isNotNull();
        assertThat(descriptionImages.size()).isEqualTo(2);
        assertThat(images.size()).isEqualTo(3);
    }

    @Test
    void findByImageRelationAndId() {
    }

    @Test
    void findByImageRelationAndForeignId() {
    }

    @Test
    void findAllByImageRelationAndForeignId() {
    }

}