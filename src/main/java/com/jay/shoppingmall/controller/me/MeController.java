package com.jay.shoppingmall.controller.me;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/me")
@Controller
public class MeController {

    @GetMapping
    public String me() {
        return "me/home";
    }

    @GetMapping("/cart")
    public String myCart() {
        return "me/cart";
    }
}
