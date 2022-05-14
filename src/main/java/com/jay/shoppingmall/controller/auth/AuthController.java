package com.jay.shoppingmall.controller.auth;

import com.jay.shoppingmall.controller.common.UserValidator;
import com.jay.shoppingmall.dto.PasswordResetRequest;
import com.jay.shoppingmall.dto.SignupRequest;
import com.jay.shoppingmall.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserValidator userValidator;

    //TODO principal이 authenticated라면 "/"으로 redirect.
    @GetMapping("/login")
    public String login(HttpSession session) {

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
    public String signupAction(@Valid SignupRequest signupRequest, BindingResult result, Model model) {

        userValidator.validate(signupRequest, result);

        if (result.hasErrors()) {
//            model.addAttribute("signupRequest", signupRequest);
            return "auth/signup";
        }

        authService.signup(signupRequest);

        return "redirect:/auth/signup-done";
    }

    @GetMapping("/signup-done")
    public String privacyAgree() {

        return "auth/signup-done";
    }
//    @PostMapping("/logout")
//    public String logout(HttpServletResponse response) {
//        expireCookie(response, "memberId");
//        return "redirect:/";
//    }
//    private void expireCookie(HttpServletResponse response, String cookieName) {
//        Cookie cookie = new Cookie(cookieName, null);
//        cookie.setMaxAge(0);
//        response.addCookie(cookie);
//    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
