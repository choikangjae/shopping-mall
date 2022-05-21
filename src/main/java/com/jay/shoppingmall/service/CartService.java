package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.cart.Cart;
import com.jay.shoppingmall.domain.cart.CartRepository;
import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.request.CartRequest;
import com.jay.shoppingmall.dto.response.CartResponse;
import com.jay.shoppingmall.exception.exceptions.AlreadyExistsException;
import com.jay.shoppingmall.exception.exceptions.ItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ImageRepository imageRepository;
    private final CartRepository cartRepository;

    public List<Cart> CartItemsList(User user) {
         return cartRepository.findByUser(user)
                .orElseThrow(()-> new UsernameNotFoundException("해당 유저가 없습니다"));
    }
    public Integer addItemToCart(Long itemId, Integer quantity, User user) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(()-> new ItemNotFoundException("해당 상품이 존재하지않습니다"));
        Image image = imageRepository.findByItemId(itemId);
        Cart cart = cartRepository.findByUserAndItem(user, item);
//                .orElseThrow(()-> new ItemNotFoundException("Cart Not Found"));

        Integer addedQuantity = quantity;
        //이미 장바구니에 상품이 존재하면
        if (cart != null) {
            throw new AlreadyExistsException("이미 상품이 장바구니에 존재합니다");
//            addedQuantity = quantity + cart.getQuantity();
//            cart.manipulateQuantity(addedQuantity);
//            cartRepository.save(cart);
        //상품이 장바구니에 존재하지 않으면
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
        Item item = itemRepository.findById(request.getId())
                .orElseThrow(()-> new ItemNotFoundException("해당 상품이 존재하지않습니다"));
        Image image = imageRepository.findByItemId(request.getId());
        Cart cart = cartRepository.findByUserAndItem(user, item);

        if (Objects.equals(request.getQuantity(), cart.getQuantity())) {
            throw new AlreadyExistsException("상품 개수가 변하지 않았습니다");
        }

        final int oldTotalPrice = request.getTotalPrice() - cart.getQuantity() * cart.getItem().getPrice();
        final Integer updatedQuantity = cart.manipulateQuantity(request.getQuantity());
        final Integer multipliedPrice = updatedQuantity * cart.getItem().getPrice();

        final Integer newTotalPrice = oldTotalPrice + updatedQuantity * cart.getItem().getPrice();

        cartRepository.saveAndFlush(cart);

        return CartResponse.builder()
                .id(cart.getId())
                .quantity(cart.getQuantity())
                .multipliedPrice(multipliedPrice)
                .totalPrice(newTotalPrice)
                .build();
    }
    //todo 상품제거하는 기능 구현하기. 삭제된 시간과 롤백 기능.
    public void deleteCart(final CartRequest request, final User user) {
        Item item = itemRepository.findById(request.getId())
                .orElseThrow(()-> new ItemNotFoundException("해당 상품이 존재하지않습니다"));
        Image image = imageRepository.findByItemId(request.getId());
        Cart cart = cartRepository.findByUserAndItem(user, item);

        cartRepository.delete(cart);

    }
}
