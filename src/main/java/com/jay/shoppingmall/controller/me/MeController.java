package com.jay.shoppingmall.controller.me;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.domain.cart.Cart;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.AgreeRequest;
import com.jay.shoppingmall.dto.MeDetailResponse;
import com.jay.shoppingmall.service.CartService;
import com.jay.shoppingmall.service.MeService;
import com.jay.shoppingmall.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/me")
@Controller
public class MeController {

    private final MeService meService;

    @GetMapping
    public String me(@CurrentUser User user, Model model) {
        model.addAttribute("user", user);

        return "me/home";
    }

    @PostMapping("/privacy")
    public String showPersonalInformationAgreementForm(@CurrentUser User user, Model model) {
        if (user == null) {
            return "redirect:/";
        }
        if (user.getAddress() != null || user.getName() != null || user.getPhoneNumber() != null) {
            return "redirect:/";
        }
        model.addAttribute("user", user);
        return "me/agreement";
    }

    @PostMapping("/privacy/agree")
    public ResponseEntity<?> agreeCheck(@Valid @RequestBody AgreeRequest agreeRequest, @CurrentUser User user) {
        if (user == null) {
            return ResponseEntity.badRequest().body(null);
        }
        Long id = user.getId();

        if (!meService.agreeCheck(agreeRequest, id)) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok().body(true);
    }

    @GetMapping("/update")
    public String meUpdate() {
        return "me/update";
    }
}
