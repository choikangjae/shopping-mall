package com.jay.shoppingmall.controller.auth;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.controller.common.UserValidator;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.PasswordResetRequest;
import com.jay.shoppingmall.dto.SignupRequest;
import com.jay.shoppingmall.security.UserAdapter;
import com.jay.shoppingmall.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private final UserDetailsService userDetailsService;

    @GetMapping("/me")
    public String getUserProfile(@CurrentUser User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("principal : " + authentication.getPrincipal());
        System.out.println("Implementing class of UserDetails: " + authentication.getPrincipal().getClass());
        System.out.println("Implementing class of UserDetailsService: " + userDetailsService.getClass());
        return "/auth/me";
    }


    //TODO principal이 authenticated라면 "/"으로 redirect.
    @GetMapping("/login")
    public String login(@CurrentUser User user) {

        if (user != null) {
            System.out.println(user);
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
