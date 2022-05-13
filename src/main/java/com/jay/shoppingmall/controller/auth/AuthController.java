package com.jay.shoppingmall.controller.auth;

import com.jay.shoppingmall.dto.PasswordResetRequest;
import com.jay.shoppingmall.dto.SignupRequest;
import com.jay.shoppingmall.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signup(SignupRequest signupRequest) {
        return "auth/signup";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(PasswordResetRequest passwordResetRequest) {
        return "/auth/forgot-password";
    }

    @PostMapping("/signup")
    public String signupAction(@Valid SignupRequest signupRequest) {
        authService.signup(signupRequest);

        return "redirect:/auth/privacy-info";
    }
}
