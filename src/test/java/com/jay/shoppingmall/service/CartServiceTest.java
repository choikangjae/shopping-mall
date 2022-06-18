package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.cart.Cart;
import com.jay.shoppingmall.domain.cart.CartRepository;
import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.item.item_option.ItemOption;
import com.jay.shoppingmall.domain.item.item_option.ItemOptionRepository;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.request.CartManipulationRequest;
import com.jay.shoppingmall.dto.response.cart.CartPricePerSellerResponse;
import com.jay.shoppingmall.dto.response.cart.CartPriceResponse;
import com.jay.shoppingmall.dto.response.cart.CartPriceTotalResponse;
import com.jay.shoppingmall.dto.response.item.ItemOptionResponse;
import com.jay.shoppingmall.exception.exceptions.AlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    @Mock
    SellerRepository sellerRepository;

    User user;
    Item item;
    ItemOption itemOption;
    List<ItemOptionResponse> list;
    ItemOptionResponse itemOptionResponse;
    Cart cart;
    Seller seller;
    List<Cart> carts;

    @BeforeEach
    void setup() {

        user = EntityBuilder.getUser();
        item = EntityBuilder.getItem();
        itemOption = EntityBuilder.getItemOption();
        seller = EntityBuilder.getSeller();

        list = new ArrayList<>();
        itemOptionResponse = ItemOptionResponse.builder()
                .itemQuantity(5)
                .itemId(item.getId())
                .itemOptionId(itemOption.getId())
                .build();
        list.add(itemOptionResponse);

        carts = new ArrayList<>();
        cart = Cart.builder()
                .itemOption(itemOption)
                .user(user)
                .item(item)
                .quantity(itemOptionResponse.getItemQuantity())
                .build();
        carts.add(cart);
    }


    @Test
    void cartSaved_Success_addItemOptionsToCart() {
        when(itemRepository.findById(itemOptionResponse.getItemId())).thenReturn(Optional.of(item));
        when(itemOptionRepository.findById(itemOptionResponse.getItemOptionId())).thenReturn(Optional.of(itemOption));

        cartService.addOptionItemsToCart(list, user);

        verify(cartRepository, times(1)).save(any());
        verify(cartRepository, times(1)).findByUserAndItemAndItemOption(user, item, itemOption);
    }

    @Test
    void cartSavedFailed_CartAlreadyExists_addItemOptionsToCart() {
        when(itemRepository.findById(itemOptionResponse.getItemId())).thenReturn(Optional.of(item));
        when(itemOptionRepository.findById(itemOptionResponse.getItemOptionId())).thenReturn(Optional.of(itemOption));
        when(cartRepository.findByUserAndItemAndItemOption(user, item, itemOption)).thenThrow(new AlreadyExistsException(""));

        assertThrows(AlreadyExistsException.class, () -> {
            cartService.addOptionItemsToCart(list, user);
        });

        verify(cartRepository, never()).save(any());
        verify(cartRepository, times(1)).findByUserAndItemAndItemOption(user, item, itemOption);
    }

    @Test
    void deleteCart() {
        CartManipulationRequest request = CartManipulationRequest.builder()
                .cartId(cart.getId())
                .build();
        when(cartRepository.findById(request.getCartId())).thenReturn(Optional.of(cart));

        cartService.deleteCart(request, user);

        verify(cartRepository, times(1)).delete(cart);
    }

    @Test
    void showCartItemsList() {
    }


    @Test
    void shouldReturn_CartPriceTotalResponse_cartPriceTotal() {
        when(cartRepository.findByUserAndIsSelectedTrue(user)).thenReturn(carts);
        when(sellerRepository.findById(any())).thenReturn(Optional.ofNullable(seller));

        final CartPriceTotalResponse cartPriceTotalResponse = cartService.cartPriceTotal(user);

        assertThat(cartPriceTotalResponse.getCartTotalPrice()).isGreaterThan(0);
        assertThat(cartPriceTotalResponse.getCartTotalQuantity()).isGreaterThan(0);
    }

    @Test
    void shouldReturn_cartPricePerSellerResponse_cartPricePerSeller() {
        when(cartRepository.findByUserAndIsSelectedTrue(user)).thenReturn(carts);
        when(sellerRepository.findById(any())).thenReturn(Optional.ofNullable(seller));

        final CartPricePerSellerResponse cartPricePerSellerResponse = cartService.cartPricePerSeller(user, seller);

        assertThat(cartPricePerSellerResponse.getSellerId()).isGreaterThan(0);
        assertThat(cartPricePerSellerResponse.getItemTotalPricePerSeller()).isGreaterThan(0);
        assertThat(cartPricePerSellerResponse.getItemTotalQuantityPerSeller()).isGreaterThan(0);
    }

    @Test
    void changeQuantity() {
        CartManipulationRequest request = CartManipulationRequest.builder()
                .isSelected(true)
                .cartId(cart.getId())
                .cartQuantity(5)
                .build();
        when(cartRepository.findById(request.getCartId())).thenReturn(Optional.ofNullable(cart));
        when(sellerRepository.findById(any())).thenReturn(Optional.ofNullable(seller));

        final CartPriceResponse cartPriceResponse = cartService.changeQuantity(request, user);

        assertThat(cart.getIsSelected()).isTrue();
        assertThat(cartPriceResponse.getCartManipulatedPrice()).isGreaterThan(0);
    }

    @Test
    void whenIsSelectedFalse_cartWillBeFalse_changeQuantity() {
        CartManipulationRequest request = CartManipulationRequest.builder()
                .isSelected(false)
                .cartId(cart.getId())
                .cartQuantity(5)
                .build();
        when(cartRepository.findById(request.getCartId())).thenReturn(Optional.ofNullable(cart));
        when(sellerRepository.findById(any())).thenReturn(Optional.ofNullable(seller));

        final CartPriceResponse cartPriceResponse = cartService.changeQuantity(request, user);

        assertThat(cart.getIsSelected()).isFalse();
        assertThat(cartPriceResponse.getCartManipulatedPrice()).isGreaterThan(0);
    }

    @Test
    void cartSelect() {
        Cart cart1 = Cart.builder()
                .quantity(5)
                .build();
        Cart cart2 = Cart.builder()
                .quantity(3)
                .build();
        List<Cart> cartList = new ArrayList<>();
        cartList.add(cart1);
        cartList.add(cart2);

        when(cartRepository.findByUser(user)).thenReturn(cartList);

        final CartPriceResponse cartPriceResponse = cartService.cartSelect("true", user);

    }

    @Test
    void testDeleteCart() {
    }

    @Test
    void whenCartsIsNotEmpty_TotalQuantityNotZero_getTotalQuantity() {
        Cart cart1 = Cart.builder()
                .quantity(5)
                .build();
        Cart cart2 = Cart.builder()
                .quantity(3)
                .build();
        List<Cart> cartList = new ArrayList<>();
        cartList.add(cart1);
        cartList.add(cart2);

        when(cartRepository.findByUser(user)).thenReturn(cartList);

        final Integer totalQuantity = cartService.getTotalQuantity(user);

        assertThat(cartList).isNotEmpty();
        assertThat(totalQuantity).isEqualTo(8);
    }

    @Test
    void whenCartsIsEmpty_TotalQuantityZero_getTotalQuantity() {
        List<Cart> cartList = new ArrayList<>();

        when(cartRepository.findByUser(user)).thenReturn(cartList);

        final Integer totalQuantity = cartService.getTotalQuantity(user);

        assertThat(cartList).isEmpty();
        assertThat(totalQuantity).isZero();
    }

    @Test
    void whenTotalPriceIsGreaterThanOrEqualToShippingFeeFreePolicy_ShippingFeeFree_shippingFeeCheck() {
        final int SHIPPING_FEE_DEFAULT = 3000;

        Seller seller = Seller.builder()
                .id(1L)
                .isSellerAgree(true)
                .shippingFeeFreePolicy(30000)
                .shippingFeeDefault(SHIPPING_FEE_DEFAULT)
                .build();

        when(sellerRepository.findById(any())).thenReturn(Optional.ofNullable(seller));

        final Integer shippingFee = cartService.shippingFeeCheck(1L, 30000L);

        assertThat(shippingFee).isEqualTo(0);
    }

    @Test
    void whenTotalPriceIsLessThanShippingFeeFreePolicy_ShippingFeeCharged_shippingFeeCheck() {
        final int SHIPPING_FEE_DEFAULT = 3000;

        Seller seller = Seller.builder()
                .id(1L)
                .isSellerAgree(true)
                .shippingFeeFreePolicy(30000)
                .shippingFeeDefault(SHIPPING_FEE_DEFAULT)
                .build();

        when(sellerRepository.findById(any())).thenReturn(Optional.ofNullable(seller));

        final Integer shippingFee = cartService.shippingFeeCheck(1L, 29999L);

        assertThat(shippingFee).isEqualTo(SHIPPING_FEE_DEFAULT);
    }

    @Test
    void whenShippingFeePolicyIsNull_AlwaysShippingFeeCharged_shippingFeeCheck() {
        final int SHIPPING_FEE_DEFAULT = 3000;

        Seller seller = Seller.builder()
                .id(1L)
                .isSellerAgree(true)
                .shippingFeeFreePolicy(null)
                .shippingFeeDefault(SHIPPING_FEE_DEFAULT)
                .build();

        when(sellerRepository.findById(any())).thenReturn(Optional.ofNullable(seller));

        final Integer shippingFee = cartService.shippingFeeCheck(1L, Long.MAX_VALUE);

        assertThat(shippingFee).isEqualTo(SHIPPING_FEE_DEFAULT);
    }
}