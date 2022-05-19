package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/order")
@Controller
public class OrderController {

    @GetMapping("/now")
    public String orderNow(@CurrentUser User user, Model model) {
        return "/order/now";
    }
}
