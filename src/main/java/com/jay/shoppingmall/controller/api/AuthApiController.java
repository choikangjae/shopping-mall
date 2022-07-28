package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.password.PasswordResetRequest;
import com.jay.shoppingmall.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
@Api(tags = "user")
public class AuthApiController {

    private final AuthService authService;

    @ApiOperation(value = "비밀번호 초기화 메일 발송", notes = "해당 이메일로 비밀번호를 초기화할 수 있는 URL이 담긴 이메일이 발송됩니다.")
    @PostMapping("/forgot-password")
    public ResponseEntity<?> passwordResetMailSend(@RequestBody PasswordResetRequest passwordResetRequest, @CurrentUser User user) {
        if (user != null) {
            return ResponseEntity.badRequest().body(null);
        }
        authService.passwordTokenSender(passwordResetRequest);

        return ResponseEntity.ok().body(null);
    }
}
