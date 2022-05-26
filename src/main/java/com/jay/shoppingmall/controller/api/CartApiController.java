package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.CartRequest;
import com.jay.shoppingmall.dto.response.CartResponse;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import com.jay.shoppingmall.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cart")
public class CartApiController {

    private final CartService cartService;

    @PostMapping("/total")
    public ResponseEntity<?> showCartTotal(@CurrentUser User user) {
        Integer totalQuantity = 0;
        if (user == null) {
            return ResponseEntity.ok(totalQuantity);
        }
        totalQuantity = cartService.getTotalQuantity(user);

        return ResponseEntity.ok(totalQuantity);
    }
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@Valid @RequestBody CartRequest request, @CurrentUser User user) {

        cartService.addItemToCart(request.getId(), request.getQuantity(), user);

        return ResponseEntity.ok().body(null);
    }
    @PostMapping("/update")
    public ResponseEntity<?> updateCart(@RequestBody CartRequest request, @CurrentUser User user) {
//        String s = request.getTotalPrice().replace(",","");
//        request.setTotalPrice(s);

        CartResponse response = cartService.updateCart(request, user);

        return ResponseEntity.ok().body(response);
    }
    @PostMapping("/delete")
    public ResponseEntity<?> deleteCart(@RequestBody CartRequest request, @CurrentUser User user) {

        CartResponse response = cartService.deleteCart(request, user);

        return ResponseEntity.ok().body(response);
    }
}
