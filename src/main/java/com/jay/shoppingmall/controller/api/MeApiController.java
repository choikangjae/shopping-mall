package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.AgreeRequest;
import com.jay.shoppingmall.dto.PasswordCheckRequest;
import com.jay.shoppingmall.service.MeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class MeApiController {

    private final MeService meService;

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

    @PostMapping("/reconfirm")
    public ResponseEntity<?> reConfirm(@RequestBody PasswordCheckRequest password, @CurrentUser User user) {
        if (password.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        if (!meService.passwordCheck(password.getPassword(), user)) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok().body(true);
    }
}
