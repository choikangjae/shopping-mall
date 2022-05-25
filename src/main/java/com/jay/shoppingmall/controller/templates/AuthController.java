package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.controller.common.SellerValidator;
import com.jay.shoppingmall.controller.common.UserValidator;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.PasswordResetRequest;
import com.jay.shoppingmall.dto.request.SignupRequest;
import com.jay.shoppingmall.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserValidator userValidator;
    private final SellerValidator sellerValidator;

    @GetMapping("/login")
    public String login(HttpServletRequest request, @CurrentUser User user) {
        if (user != null) {
            return "redirect:/";
        }

        return "auth/login";
    }
    @PostMapping("/login")
    public String loginAction(HttpServletRequest request, Model model, @CurrentUser User user) {
        if (user != null) {
            return "redirect:/";
        }
        model.addAttribute("username", request.getAttribute("username"));

        return "auth/login";
    }

    @GetMapping("/signup")
    public String signup(SignupRequest signupRequest, @CurrentUser User user) {
        if (user != null) {
            return "redirect:/";
        }
        return "auth/signup";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(PasswordResetRequest passwordResetRequest, @CurrentUser User user) {
        if (user != null) {
            return "redirect:/";
        }
        return "/auth/forgot-password";
    }

    @PostMapping("/signup")
    public String signupAction(@Valid SignupRequest signupRequest, BindingResult result) {
        sellerValidator.validate(signupRequest, result);

        if (result.hasErrors()) {
            return "auth/signup";
        }

        authService.signup(signupRequest);

        return "redirect:/auth/signup-done";
    }

    @GetMapping("/seller-signup")
    public String sellerSignup(SignupRequest signupRequest, @CurrentUser User user) {
//        if (user == null) {
//            return "redirect:/auth/login";
//        }
//        user.getAgree().getIsMandatoryAgree().equals(false);
        return "auth/seller-signup";
    }
    @PostMapping("/seller-signup")
    public String sellerSignupAction(@Valid SignupRequest signupRequest, BindingResult result, Model model) {
        userValidator.validate(signupRequest, result);

        if (result.hasErrors()) {
//            model.addAttribute("signupRequest", signupRequest);
            return "auth/seller-signup";
        }

        authService.sellerSignup(signupRequest);

        return "redirect:/auth/signup-done";
    }

    @GetMapping("/signup-done")
    public String privacyAgree() {

        return "auth/signup-done";
    }

}
