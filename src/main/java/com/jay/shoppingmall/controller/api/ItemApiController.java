package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.CartRequest;
import com.jay.shoppingmall.dto.response.CartResponse;
import com.jay.shoppingmall.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cart")
public class ItemApiController {

    private final CartService cartService;

    //cart에서 재고를 비교하고 거기서 에러 발생.
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartRequest request, @CurrentUser User user, HttpServletResponse response) {

        cartService.addItemToCart(request.getId(), request.getQuantity(), user);

        return ResponseEntity.ok().body(null);
    }
    @PostMapping("/update")
    public ResponseEntity<?> updateCart(@RequestBody CartRequest request, @CurrentUser User user) {

        CartResponse response = cartService.updateCart(request, user);

        return ResponseEntity.ok().body(response);
    }
    @PostMapping("/delete")
    public ResponseEntity<?> deleteCart(@RequestBody CartRequest request, @CurrentUser User user) {

        cartService.deleteCart(request, user);

        return ResponseEntity.ok().body(null);
    }
}
