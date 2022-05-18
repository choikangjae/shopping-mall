package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.cart.Cart;
import com.jay.shoppingmall.domain.cart.CartRepository;
import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.exception.ItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ImageRepository imageRepository;
    private final CartRepository cartRepository;

    public List<Cart> CartItemsList(User user) {
         return cartRepository.findByUser(user)
                .orElseThrow(()-> new UsernameNotFoundException("No User Found"));
    }
    public Integer addItemToCart(Long itemId, Integer quantity, User user) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(()-> new ItemNotFoundException("Item Not Found"));
        Image image = imageRepository.findByItemId(itemId);
        Cart cart = cartRepository.findByUserAndItem(user, item);
//                .orElseThrow(()-> new ItemNotFoundException("Cart Not Found"));

        Integer addedQuantity = quantity;
        //이미 장바구니에 상품이 존재하면
        if (cart != null) {
            addedQuantity = quantity + cart.getQuantity();
            cart.manipulateQuantity(addedQuantity);
            cartRepository.save(cart);
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
}
