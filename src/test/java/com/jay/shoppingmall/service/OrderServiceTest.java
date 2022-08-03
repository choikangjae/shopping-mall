package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    OrderService orderService;
    @Mock
    CartService cartService;

    User user;

    @BeforeEach
    void setUp() {
        user = EntityBuilder.getUser();
    }

    @Test
    void orderProcess() {
//        when(cartService.showCartItemsList(any())).thenReturn()
//        orderService.orderProcess(user);
    }

    @Test
    void showOrderDetail() {
    }

    @Test
    void showOrders() {
    }
}