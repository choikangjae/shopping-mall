package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.password.PasswordResetRequest;
import com.jay.shoppingmall.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthApiController {

    private final AuthService authService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> passwordResetMailSend(@RequestBody PasswordResetRequest passwordResetRequest, @CurrentUser User user) {
        if (user != null) {
            return ResponseEntity.badRequest().body(null);
        }
        authService.passwordTokenSender(passwordResetRequest);

        return ResponseEntity.ok().body(null);
    }
}
