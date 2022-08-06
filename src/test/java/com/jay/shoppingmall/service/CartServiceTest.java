package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.cart.Cart;
import com.jay.shoppingmall.domain.cart.CartRepository;
import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRepository;
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
import com.jay.shoppingmall.dto.response.item.ItemAndQuantityResponse;
import com.jay.shoppingmall.dto.response.item.ItemOptionResponse;
import com.jay.shoppingmall.dto.response.seller.SellerResponse;
import com.jay.shoppingmall.exception.exceptions.AlreadyExistsException;
import com.jay.shoppingmall.exception.exceptions.NotValidException;
import com.jay.shoppingmall.service.handler.FileHandler;
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
import java.util.Map;
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
    FileHandler fileHandler;
    @Mock
    ImageRepository imageRepository;

    User user;
    Item item;
    Item item2;

    ItemOption itemOption;
    List<ItemOptionResponse> list;
    ItemOptionResponse itemOptionResponse;

    Cart cart;
    Cart cart2;
    Cart cart3;

    Seller seller;
    List<Cart> carts;

    @BeforeEach
    void setup() {

        user = EntityBuilder.getUser();

        item2 = EntityBuilder.getItem2();
        itemOption = EntityBuilder.getItemOption();
        seller = EntityBuilder.getSeller();

        item = Item.builder()
                .seller(seller)
                .id(0L)
                .build();

        list = new ArrayList<>();
        itemOptionResponse = ItemOptionResponse.builder()
                .itemQuantity(5)
                .itemId(item.getId())
                .itemOptionId(itemOption.getId())
                .build();
        list.add(itemOptionResponse);

        carts = new ArrayList<>();
        cart = Cart.builder()
                .isSelected(true)
                .itemOption(itemOption)
                .user(user)
                .item(item)
                .quantity(itemOptionResponse.getItemQuantity())
                .build();
        cart2 = Cart.builder()
                .isSelected(true)
                .itemOption(itemOption)
                .user(user)
                .item(item)
                .quantity(itemOptionResponse.getItemQuantity())
                .build();
        cart3 = Cart.builder()
                .isSelected(false)
                .itemOption(itemOption)
                .user(user)
                .item(item)
                .quantity(itemOptionResponse.getItemQuantity())
                .build();
        carts.add(cart);
        carts.add(cart2);
        carts.add(cart3);
    }


    @Test
    void cartSaved_Success_addItemOptionsToCart() {
        when(itemRepository.findById(itemOptionResponse.getItemId())).thenReturn(Optional.of(item));
        when(itemOptionRepository.findById(itemOptionResponse.getItemOptionId())).thenReturn(Optional.of(itemOption));

        cartService.addOptionItemsToCart(list, user);

        verify(cartRepository, times(1)).save(any());
    }

    @Test
    void cartSaveFailed_CartAlreadyExists_addItemOptionsToCart() {
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

        Image image = mock(Image.class);
        List<Image> images = new ArrayList<>();
        images.add(image);
        images.add(image);
        when(cartRepository.findByUser(any())).thenReturn(carts);
        when(imageRepository.findByImageRelationAndForeignIdIn(any(), any())).thenReturn(images);
        when(fileHandler.getStringImage(any(Image.class))).thenReturn("이미지");

        final Map<SellerResponse, List<ItemAndQuantityResponse>> sellerResponseListMap = cartService.showCartItemsList(user);

        assertThat(sellerResponseListMap.size()).isGreaterThan(0);
    }


    @Test
    void shouldReturn_CartPriceTotalResponse_cartPriceTotal() {
        when(cartRepository.findByUser(any())).thenReturn(carts);

        final CartPriceTotalResponse cartPriceTotalResponse = cartService.cartPriceTotal(user);

        assertThat(cartPriceTotalResponse.getCartTotalPrice()).isEqualTo(30000);
        assertThat(cartPriceTotalResponse.getCartTotalQuantity()).isEqualTo(10);
    }

    @Test
    void whenCartIsSelectedFalse_NotIncluded_cartPriceTotal() {
        Cart cart = Cart.builder()
                .isSelected(false)
                .itemOption(itemOption)
                .user(user)
                .item(item)
                .quantity(itemOptionResponse.getItemQuantity())
                .build();
        Cart cart2 = Cart.builder()
                .isSelected(false)
                .itemOption(itemOption)
                .user(user)
                .item(item)
                .quantity(itemOptionResponse.getItemQuantity())
                .build();
        Cart cart3 = Cart.builder()
                .isSelected(false)
                .itemOption(itemOption)
                .user(user)
                .item(item)
                .quantity(itemOptionResponse.getItemQuantity())
                .build();
        List<Cart> carts = new ArrayList<>();
        carts.add(cart);
        carts.add(cart2);
        carts.add(cart3);

        when(cartRepository.findByUser(any())).thenReturn(carts);

        final CartPriceTotalResponse cartPriceTotalResponse = cartService.cartPriceTotal(user);

        assertThat(cartPriceTotalResponse.getCartTotalPrice()).isZero();
        assertThat(cartPriceTotalResponse.getCartTotalQuantity()).isZero();
    }

    @Test
    void changeQuantity() {
        CartManipulationRequest request = CartManipulationRequest.builder()
                .isSelected(true)
                .cartId(cart.getId())
                .cartQuantity(5)
                .build();
        when(cartRepository.findById(request.getCartId())).thenReturn(Optional.ofNullable(cart));

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

        final CartPriceResponse cartPriceResponse = cartService.changeQuantity(request, user);

        assertThat(cart.getIsSelected()).isFalse();
        assertThat(cartPriceResponse.getCartManipulatedPrice()).isGreaterThan(0);
    }

    @Test
    void whenCheckIsElseThanTrueOrFalse_ThrowNotValidException_cartSelectAll() {

        assertThrows(NotValidException.class, () -> cartService.cartSelectAll("notTrueNotFalse", user));
    }

    @Test
    void whenCheckIsTrue_SelectAll_cartSelectAll() {
        Cart cart1 = Cart.builder()
                .quantity(5)
                .item(item)
                .itemOption(itemOption)
                .build();
        Cart cart2 = Cart.builder()
                .quantity(3)
                .item(item2)
                .itemOption(itemOption)
                .build();
        List<Cart> cartList = new ArrayList<>();
        cartList.add(cart1);
        cartList.add(cart2);

        when(cartRepository.findByUser(user)).thenReturn(cartList);

        final CartPriceResponse cartPriceResponse = cartService.cartSelectAll("true", user);

        assertThat(cartPriceResponse.getCartPriceTotalResponse().getCartTotalQuantity()).isEqualTo(8);
    }

    @Test
    void whenCheckIsFalse_DeselectAll_cartSelectAll() {
        Cart cart1 = Cart.builder()
                .quantity(5)
                .item(item)
                .itemOption(itemOption)
                .build();
        Cart cart2 = Cart.builder()
                .quantity(3)
                .item(item2)
                .itemOption(itemOption)
                .build();
        List<Cart> cartList = new ArrayList<>();
        cartList.add(cart1);
        cartList.add(cart2);

        when(cartRepository.findByUser(user)).thenReturn(cartList);

        final CartPriceResponse cartPriceResponse = cartService.cartSelectAll("false", user);

        assertThat(cartPriceResponse.getCartPriceTotalResponse().getCartTotalQuantity()).isEqualTo(0);
        assertThat(cartPriceResponse.getCartPricePerSellerResponses().get(0).getItemTotalPricePerSeller()).isEqualTo(0);
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

        final Integer shippingFee = cartService.shippingFeeCheck(seller, 30000L);

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

        final Integer shippingFee = cartService.shippingFeeCheck(seller, 29999L);

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

        final Integer shippingFee = cartService.shippingFeeCheck(seller, Long.MAX_VALUE);

        assertThat(shippingFee).isEqualTo(SHIPPING_FEE_DEFAULT);
    }

    @Test
    void whenDistinctSellerIsTwo_SizeIsTwo_cartPricePerSeller() {
        Seller seller2 = Seller.builder()
                .build();
        Item seller2Item = Item.builder()
                .seller(seller2)
                .build();
        Cart seller2ItemCart = Cart.builder()
                .isSelected(true)
                .itemOption(itemOption)
                .quantity(10)
                .item(seller2Item)
                .build();

        carts.add(seller2ItemCart);
        when(cartRepository.findByUser(any())).thenReturn(carts);

        final List<CartPricePerSellerResponse> cartPricePerSellerResponses = cartService.cartPricePerSeller(user);

        assertThat(cartPricePerSellerResponses.size()).isEqualTo(2);
    }
}