package com.jay.shoppingmall.domain.cart;

import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.item.item_option.ItemOption;
import com.jay.shoppingmall.domain.item.item_option.ItemOptionRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.NonUniqueResultException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemOptionRepository itemOptionRepository;

    User user;
    User user2;
    Item item;
    Item item2;
    ItemOption itemOption;
    Cart cart;
    Cart cart2;
    Cart cart3;
    Cart cart4;

    @BeforeEach
    void setUp() {
        user = EntityBuilder.getUser();
        user2 = EntityBuilder.getUser2();
        item = EntityBuilder.getItem();
        item2 = EntityBuilder.getItem2();
        itemOption = EntityBuilder.getItemOption();


        cart = Cart.builder()
                .user(user)
                .item(item)
                .itemOption(itemOption)
                .isSelected(true)
                .quantity(5)
                .build();
        cart2 = Cart.builder()
                .user(user)
                .item(item2)
                .itemOption(itemOption)
                .isSelected(false)
                .quantity(5)
                .build();
        cart3 = Cart.builder()
                .user(user2)
                .item(item)
                .itemOption(itemOption)
                .isSelected(true)
                .quantity(5)
                .build();
        cart4 = Cart.builder()
                .user(user2)
                .item(item2)
                .itemOption(itemOption)
                .isSelected(false)
                .quantity(5)
                .build();

        itemRepository.save(item);
        itemRepository.save(item2);
        itemOptionRepository.save(itemOption);
        userRepository.save(user);
        userRepository.save(user2);
        cartRepository.save(cart);
        cartRepository.save(cart2);
        cartRepository.save(cart3);
        cartRepository.save(cart4);
    }

    @Test
    void itShouldReturnCartList_findByUser() {
        final List<Cart> carts = cartRepository.findByUser(user);

        assertThat(carts).isNotEmpty();
        assertThat(carts.get(0).getQuantity()).isGreaterThan(0);
    }
    @Test
    void cartIsEmpty_findByUser() {
        final List<Cart> carts = cartRepository.findByUser(any(User.class));

        assertThat(carts).isEmpty();
    }

    @Test
    void isShouldReturnCartList_findByUserAndIsSelectedTrue() {
        final List<Cart> carts = cartRepository.findByUserAndIsSelectedTrue(user);

        assertThat(carts).isNotEmpty();
        assertThat(carts.size()).isEqualTo(1);
    }

    @Test
    void shouldUnique_ButDuplicated_findByUserAndItemAndItemOption() {
        Cart dupCart = Cart.builder()
                .user(user)
                .item(item)
                .itemOption(itemOption)
                .isSelected(true)
                .quantity(5)
                .build();
        cartRepository.save(dupCart);

        assertThrows(RuntimeException.class, () -> cartRepository.findByUserAndItemAndItemOption(user, item, itemOption));
    }
    @Test
    void findByUserAndItemAndItemOption() {
        final Optional<Cart> cart = cartRepository.findByUserAndItemAndItemOption(user, item, itemOption);

        assertThat(cart).isNotEmpty();
        assertThat(cart).isNotEqualTo(Optional.empty());
    }

    @Test
    void deleteByUserIdAndItemId() {
        final List<Cart> cartsBefore = cartRepository.findByUser(user);
        assertThat(cartsBefore.size()).isEqualTo(2);

        cartRepository.deleteByUserIdAndItemId(user.getId(), item.getId());

        final List<Cart> cartsAfter = cartRepository.findByUser(user);
        assertThat(cartsAfter.size()).isEqualTo(1);
    }
}