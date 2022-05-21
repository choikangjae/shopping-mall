package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.controller.common.CurrentUser;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserValidator userValidator;

    @GetMapping("/login")
    public String login(HttpServletRequest request, @CurrentUser User user) {
        if (user != null) {
            return "redirect:/";
        }
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

//    @PostMapping("/logout")
//    public String logout(HttpSession session) {
//        session.setAttribute("email","");
//        return "redirect:/";
//    }
}
