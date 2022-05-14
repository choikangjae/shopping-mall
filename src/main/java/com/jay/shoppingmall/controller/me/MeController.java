package com.jay.shoppingmall.controller.me;

import com.jay.shoppingmall.dto.MeDetailResponse;
import com.jay.shoppingmall.service.MeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/me")
@Controller
public class MeController {

    private final MeService meService;

    @GetMapping
    public String me(Model model) {
//        MeDetailResponse meDetailResponse = meService.findById(id);
//        model.addAttribute("meDetailResponse", meDetailResponse);
        return "me/home";
    }

    @GetMapping("/cart")
    public String myCart() {
        return "me/cart";
    }

    @GetMapping("/update")
    public String meUpdate() {
        return "me/update";
    }
}
