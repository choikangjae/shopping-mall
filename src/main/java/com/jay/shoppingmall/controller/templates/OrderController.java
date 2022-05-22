package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.response.CartOrderResponse;
import com.jay.shoppingmall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/order")
@Controller
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/now")
    public String orderNow(@CurrentUser User user, Model model) {
        if (user == null) {
            return "redirect:/auth/login";
        }
        return "/order/now";
    }
    @GetMapping("/process")
    public String orderProcess(@CurrentUser User user, Model model) {
        List<CartOrderResponse> cartOrderResponses = orderService.orderProcess(user);
        Integer orderTotalPrice = orderService.orderTotalPrice(user);
        Integer orderTotalCount = orderService.orderTotalCount(user);

        model.addAttribute("cartOrderResponses", cartOrderResponses);
        model.addAttribute("user", user);
        model.addAttribute("orderTotalPrice", orderTotalPrice);
        model.addAttribute("orderTotalCount", orderTotalCount);

        return "/order/process";
    }
    @GetMapping("/payment")
    public String orderPayment(@CurrentUser User user, Model model) {
        List<CartOrderResponse> cartOrderResponses = orderService.orderProcess(user);
        Integer orderTotalPrice = orderService.orderTotalPrice(user);
        Integer orderTotalCount = orderService.orderTotalCount(user);

        model.addAttribute("cartOrderResponses", cartOrderResponses);
        model.addAttribute("user", user);
        model.addAttribute("orderTotalPrice", orderTotalPrice);
        model.addAttribute("orderTotalCount", orderTotalCount);

        return "/order/payment";
    }
}
