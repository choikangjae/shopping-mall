package com.jay.shoppingmall.demo;

import com.jay.shoppingmall.domain.user.Role;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.UserValidationRequest;
import com.jay.shoppingmall.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Random;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
//TODO 데모용
public class DemoController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/demo/login/user")
    public String demoLogin1(HttpSession session) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                "demo@user", "password123"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        session.setAttribute("demo", "password123");
        return "redirect:/";
    }

    @PostMapping("/demo/login/seller")
    public String demoLogin2(HttpSession session) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                "demo@seller", "password123"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        session.setAttribute("demo", "password123");
        return "redirect:/";
    }

    @PostMapping("/demo/login/seller2")
    public String demoLogin4(HttpSession session) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                "demo@seller2", "password123"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        session.setAttribute("demo", "password123");
        return "redirect:/";
    }

    @PostMapping("/demo/login/admin")
    public String demoLogin3() {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                "demo@admin", "specialPassWorD123"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "redirect:/";
    }


    @PostMapping("/random-user-generator")
    public String randomUserGenerator() {
        String email = UUID.randomUUID() + "@" + UUID.randomUUID();
        String password = UUID.randomUUID().toString();

        UserValidationRequest userValidationRequest = UserValidationRequest.builder()
                .email(email)
                .password(password)
                .repeatPassword(password)
                .build();

        authService.userRegistration(userValidationRequest);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userValidationRequest.getEmail(), userValidationRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/";
    }
}
