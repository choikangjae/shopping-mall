package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.response.item.ItemAndQuantityResponse;
import com.jay.shoppingmall.dto.response.seller.SellerResponse;
import com.jay.shoppingmall.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String showCart(Model model, @CurrentUser User user) {
        if (user == null) {
            return "/auth/login";
        }
        final Map<SellerResponse, List<ItemAndQuantityResponse>> sellerResponseListMap = cartService.showCartItemsList(user);
        model.addAttribute("sellerResponseListMap", sellerResponseListMap);
        return "/me/cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("id") Long itemId, @RequestParam("quantity") Integer quantity,
                            @CurrentUser User user, Model model) {

        if (user == null) {
            return "redirect:/auth/login";
        }

//        try {
//            cartService.addItemToCart(itemId, quantity, user);
//        } catch (Exception e) {
////            model.addAttribute("error", e.getMessage());
//            return "redirect:/item/details/" + itemId;
//        }

//        redirectAttributes.addFlashAttribute("message", "장바구니에 상품이 추가되었습니다");

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
