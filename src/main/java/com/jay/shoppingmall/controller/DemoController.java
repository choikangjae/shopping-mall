package com.jay.shoppingmall.controller;

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

import java.util.Random;
import java.util.UUID;

@Controller
@RequestMapping("/random-user-generator")
@RequiredArgsConstructor
public class DemoController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @PostMapping
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
