package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.response.item.ItemAndQuantityResponse;
import com.jay.shoppingmall.service.CartService;
import com.jay.shoppingmall.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
        final List<ItemAndQuantityResponse> itemAndQuantityResponses = cartService.CartItemsList(user);
        int totalPrice = 0;
        int totalQuantity = 0;
        for (ItemAndQuantityResponse response : itemAndQuantityResponses) {
            totalPrice += response.getPrice() * response.getQuantity();
            totalQuantity += response.getQuantity();
        }
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("items", itemAndQuantityResponses);
        return "/me/cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("id") Long itemId, @RequestParam("quantity") Integer quantity,
                            @CurrentUser User user, Model model, RedirectAttributes redirectAttributes) {

        if (user == null) {
            return "redirect:/auth/login";
        }

        try {
            cartService.addItemToCart(itemId, quantity, user);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/item/details/" + itemId;
        }

        redirectAttributes.addFlashAttribute("message", "장바구니에 상품이 추가되었습니다");

        return "redirect:/cart";
    }
//    @PostMapping("/add")
//    public String addToCart(@Valid @RequestBody CartRequest request, @CurrentUser User user) {
//        System.out.println(user.getEmail());
//        if (user == null) {
////            throw new UserNotFoundException("로그인이 필요한 서비스입니다");
//            return "redirect:/";
//        }
//
//        cartService.addItemToCart(request.getId(), request.getQuantity(), user);
//
//        return "redirect:/cart";
//    }
}
