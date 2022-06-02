package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.cart.Cart;
import com.jay.shoppingmall.domain.cart.CartRepository;
import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.CartRequest;
import com.jay.shoppingmall.dto.response.cart.CartResponse;
import com.jay.shoppingmall.dto.response.item.ItemAndQuantityResponse;
import com.jay.shoppingmall.exception.exceptions.AlreadyExistsException;
import com.jay.shoppingmall.exception.exceptions.ItemNotFoundException;
import com.jay.shoppingmall.service.handler.FileHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final ItemRepository itemRepository;
    private final ImageRepository imageRepository;
    private final CartRepository cartRepository;
    private final FileHandler fileHandler;

    public List<ItemAndQuantityResponse> showCartItemsList(User user) {
        final List<Cart> carts = cartRepository.findByUser(user)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저가 없습니다"));
        List<ItemAndQuantityResponse> itemAndQuantityResponses = new ArrayList<>();

        for (Cart cart : carts) {

            itemAndQuantityResponses.add(ItemAndQuantityResponse.builder()
                            .id(cart.getItem().getId())
                            .name(cart.getItem().getName())
                            .price(cart.getItem().getPrice())
                            .salePrice(cart.getItem().getSalePrice())
                            .image(fileHandler.getStringImage(imageRepository.findByItemIdAndIsMainImageTrue(cart.getItem().getId())))
                            .zzim(cart.getItem().getZzim())
                            .quantity(cart.getQuantity())
                    .build());
        }
        return itemAndQuantityResponses;
    }
    public Integer addItemToCart(Long itemId, Integer quantity, User user) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(()-> new ItemNotFoundException("해당 상품이 존재하지않습니다"));
        Cart cart = cartRepository.findByUserAndItem(user, item);

        if (item.getStock() == 0) {
            throw new ItemNotFoundException("죄송합니다. 해당 상품은 품절되었습니다");
        }
        Integer addedQuantity = quantity;
        if (cart != null) {
            throw new AlreadyExistsException("이미 상품이 장바구니에 존재합니다");
//            addedQuantity = quantity + cart.getQuantity();
//            cart.manipulateQuantity(addedQuantity);
//            cartRepository.save(cart);
        } else {
            Cart newCart = Cart.builder()
                    .user(user)
                    .item(item)
                    .quantity(addedQuantity)
                    .build();
            cartRepository.save(newCart);
        }
        return addedQuantity;
    }

    public CartResponse updateCart(final CartRequest request, final User user) {
//        int IntTotalPrice = Integer.parseInt(request.getTotalPrice());
        Item item = itemRepository.findById(request.getId())
                .orElseThrow(()-> new ItemNotFoundException("해당 상품이 존재하지않습니다"));
//        Image image = imageRepository.findByItemId(request.getId());
        Cart cart = cartRepository.findByUserAndItem(user, item);

        if (Objects.equals(request.getQuantity(), cart.getQuantity())) {
            throw new AlreadyExistsException("상품 개수가 변동되지 않았습니다");
        }

        final int oldTotalPrice = request.getTotalPrice() - cart.getQuantity() * cart.getItem().getPrice();
        final int multipliedPrice = cart.manipulateQuantity(request.getQuantity()) * cart.getItem().getPrice();
        final Integer newTotalPrice = oldTotalPrice + multipliedPrice;

        cartRepository.saveAndFlush(cart);

        return CartResponse.builder()
                .id(cart.getId())
                .quantity(cart.getQuantity())
                .multipliedPrice(multipliedPrice)
                .totalPrice(newTotalPrice)
                .totalQuantity(this.getTotalQuantity(user))
                .build();
    }
    //삭제된 시간과 롤백 기능.
    public CartResponse deleteCart(final CartRequest request, final User user) {
        Item item = itemRepository.findById(request.getId())
                .orElseThrow(()-> new ItemNotFoundException("해당 상품이 존재하지않습니다"));
        Cart cart = cartRepository.findByUserAndItem(user, item);
        if (cart == null) {
            throw new ItemNotFoundException("해당 상품이 존재하지않습니다");
        }
        final int totalPrice = request.getTotalPrice() - cart.getQuantity() * cart.getItem().getPrice();

        cartRepository.delete(cart);

        return CartResponse.builder()
                .id(cart.getId())
                .totalPrice(totalPrice)
                .totalQuantity(this.getTotalQuantity(user))
                .build();
    }

    public Integer getTotalQuantity(final User user) {
        List<Cart> carts = cartRepository.findByUser(user)
                .orElseThrow(()-> new UsernameNotFoundException("해당 유저가 없습니다"));
        Integer totalQuantity = 0;
        for (Cart cart : carts) {
            totalQuantity += cart.getQuantity();
        }
        return totalQuantity;
    }
}
