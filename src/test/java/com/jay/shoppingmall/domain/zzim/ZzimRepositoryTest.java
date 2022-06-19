//package com.jay.shoppingmall.domain.zzim;
//
//import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
//import com.jay.shoppingmall.domain.item.Item;
//import com.jay.shoppingmall.domain.item.ItemRepository;
//import com.jay.shoppingmall.domain.seller.Seller;
//import com.jay.shoppingmall.domain.seller.SellerRepository;
//import com.jay.shoppingmall.domain.user.User;
//import com.jay.shoppingmall.domain.user.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//
//@DataJpaTest
//class ZzimRepositoryTest {
//
//    @Autowired
//    ZzimRepository zzimRepository;
//    @Autowired
//    UserRepository userRepository;
//    @Autowired
//    ItemRepository itemRepository;
//    @Autowired
//    SellerRepository sellerRepository;
//
//    User user;
//    Zzim zzimTrue;
//    Zzim zzimFalse;
//    Item item;
//    Seller seller;
//    @BeforeEach
//    void setUp() {
//        user = EntityBuilder.getUser();
//        item = EntityBuilder.getItem();
//        seller = EntityBuilder.getSeller();
//
//        zzimTrue = Zzim.builder()
//                .item(item)
//                .user(user)
//                .isZzimed(true)
//                .build();
//        zzimFalse = Zzim.builder()
//                .item(item)
//                .user(user)
//                .isZzimed(false)
//                .build();
//        userRepository.save(user);
//        sellerRepository.save(seller);
//        itemRepository.save(item);
//        zzimRepository.save(zzimTrue);
//        zzimRepository.save(zzimFalse);
//    }
//
//    @Test
//    void findByUser() {
//        final Page<Zzim> zzims = zzimRepository.findByUser(user, Pageable.unpaged());
//
//        assertThat(zzims.getContent().size()).isEqualTo(1);
//    }
//
//    @Test
//    void findByUserIdAndItemId() {
//
//    }
//}