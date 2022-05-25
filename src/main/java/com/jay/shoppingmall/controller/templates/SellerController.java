package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.WriteItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/seller")
@Controller
public class SellerController {

//    private final SellerService sellerService;
    @GetMapping
    public String sellerHome() {
        return "seller/seller-home";
    }
    @GetMapping("/write")
    public String adminWrite(WriteItemRequest writeItemRequest) {
        return "seller/seller-write-item";
    }
    @GetMapping("/start")
    public String sellerStart() {
        return "seller/seller-start";
    }
    @GetMapping("/agree")
    public String sellerAgree(@CurrentUser User user, Model model) {

        model.addAttribute("user", user);
        return "seller/seller-agreement";
    }
}

