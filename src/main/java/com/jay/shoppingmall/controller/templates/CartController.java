package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.domain.cart.Cart;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.service.CartService;
import com.jay.shoppingmall.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @GetMapping
    public String showCart(Model model,
                           @CurrentUser User user) {

        if (user == null) {
            return "/auth/login";
        }
        final List<Cart> cartList = cartService.CartItemsList(user);

        model.addAttribute("cartList", cartList);

        return "/me/cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("id") Long itemId, @RequestParam("quantity") Integer quantity,
                            @CurrentUser User user, RedirectAttributes redirectAttributes) {

        if (user == null) {
            return "/auth/login";
        }

        final Integer addedQuantity = cartService.addItemToCart(itemId, quantity, user);

        redirectAttributes.addFlashAttribute("message", "장바구니에 상품이 추가되었습니다");

        return "redirect:/cart";
    }
}
