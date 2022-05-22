package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.cart.Cart;
import com.jay.shoppingmall.domain.cart.CartRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.response.CartOrderResponse;
import com.jay.shoppingmall.dto.response.CartResponse;
import com.jay.shoppingmall.exception.exceptions.CartEmptyException;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderService {

    private final CartRepository cartRepository;

    public List<CartOrderResponse> orderProcess(User user) {

        List<Cart> cartList = cartRepository.findByUser(user).orElseThrow(() -> new UserNotFoundException("잘못된 접근입니다"));
        if (cartList.isEmpty()) {
            throw new CartEmptyException("장바구니가 비어있습니다");
        }
        List<CartOrderResponse> cartOrderResponses = new ArrayList<>();

        for (Cart cart : cartList) {
            cartOrderResponses.add(CartOrderResponse.builder()
                    .item(cart.getItem())
                    .quantity(cart.getQuantity())
                    .build());

        }
        return cartOrderResponses;
    }
    public Integer orderTotalPrice(User user) {
        int totalPrice = 0;
        List<Cart> cartList = cartRepository.findByUser(user).orElseThrow(() -> new UserNotFoundException("잘못된 접근입니다"));
        if (cartList.isEmpty()) {
            throw new CartEmptyException("장바구니가 비어있습니다");
        }
        for (Cart cart : cartList) {
            totalPrice += cart.getQuantity() * cart.getItem().getPrice();
        }
        return totalPrice;
    }
    public Integer orderTotalCount(User user) {
        int totalCount = 0;
        List<Cart> cartList = cartRepository.findByUser(user).orElseThrow(() -> new UserNotFoundException("잘못된 접근입니다"));
        if (cartList.isEmpty()) {
            throw new CartEmptyException("장바구니가 비어있습니다");
        }
        for (Cart cart : cartList) {
            totalCount += cart.getQuantity();
        }
        return totalCount;
    }
}
