package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.cart.Cart;
import com.jay.shoppingmall.domain.cart.CartRepository;
import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.item.item_option.ItemOption;
import com.jay.shoppingmall.domain.item.item_option.ItemOptionRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.response.item.ItemOptionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    CartService cartService;

    @Mock
    CartRepository cartRepository;
    @Mock
    ItemOptionRepository itemOptionRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;

    User user;
    Item item;
    ItemOption itemOption;

    @BeforeEach
    void setup() {

        user = EntityBuilder.getUser();
        item = EntityBuilder.getItem();
        itemOption = EntityBuilder.getItemOption();
    }

    @Test
    void addItemOptionsToCart() {
        List<ItemOptionResponse> list = new ArrayList<>();
        ItemOptionResponse itemOptionResponse = ItemOptionResponse.builder()
                .itemQuantity(5)
                .itemId(item.getId())
                .itemOptionId(itemOption.getId())
                .build();
        list.add(itemOptionResponse);

//        Cart cart = Cart.builder()
//                .itemOption(itemOption)
//                .user(user)
//                .item(item)
//                .quantity(itemOptionResponse.getItemQuantity())
//                .build();

        when(itemRepository.findById(itemOptionResponse.getItemId())).thenReturn(Optional.of(item));
        when(itemOptionRepository.findById(itemOptionResponse.getItemOptionId())).thenReturn(Optional.of(itemOption));

        cartService.addOptionItemsToCart(list, user);

        verify(cartRepository, times(1)).save(any());
        verify(cartRepository, times(1)).findByUserAndItemAndItemOption(user, item, itemOption);
    }

    @Test
    void deleteCart() {

    }

    @Test
    void showCartItemsList() {
    }


    @Test
    void cartPriceTotal() {
    }

    @Test
    void cartPricePerSeller() {
    }

    @Test
    void changeQuantity() {
    }

    @Test
    void cartSelect() {
    }

    @Test
    void testDeleteCart() {
    }

    @Test
    void getTotalQuantity() {
    }

    @Test
    void shippingFeeCheck() {
    }
}