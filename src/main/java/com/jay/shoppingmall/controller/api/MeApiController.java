package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.AgreeRequest;
import com.jay.shoppingmall.dto.PasswordCheckRequest;
import com.jay.shoppingmall.dto.UserUpdateRequest;
import com.jay.shoppingmall.dto.UserUpdateResponse;
import com.jay.shoppingmall.exception.ErrorResponse;
import com.jay.shoppingmall.exception.exceptions.AgreeException;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import com.jay.shoppingmall.security.UserAdapter;
import com.jay.shoppingmall.service.MeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class MeApiController {

    private final MeService meService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/privacy/agree")
    public ResponseEntity<?> agreeCheck(@Valid @RequestBody AgreeRequest agreeRequest, @CurrentUser User user, HttpServletRequest request) {
        if (user == null) {
            throw new UserNotFoundException("잘못된 요청입니다");
        }
        Long id = user.getId();

        if (!meService.agreeCheck(agreeRequest, id)) {
            throw new AgreeException("잘못된 요청입니다");
        }
        if (agreeRequest.getIsMarketingAgree()) {
            request.getSession().setAttribute("isMarketingAgree", "true");
        }
        return ResponseEntity.ok().body(true);
    }

    @PostMapping("/reconfirm")
    public ResponseEntity<?> reConfirm(@RequestBody PasswordCheckRequest password, @CurrentUser User user, HttpServletRequest request) {
        if (password.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        if (!meService.passwordCheck(password.getPassword(), user)) {
            return ResponseEntity.badRequest().body(null);
        }
        request.getSession().setAttribute("password", password.getPassword());

        return ResponseEntity.ok().body(true);
    }

    @PostMapping("/privacy/update")
    public ResponseEntity<ErrorResponse> updateMe(@Valid @RequestBody UserUpdateRequest request,
                                                  @CurrentUser User user, HttpServletRequest servletRequest) {
        Long id = user.getId();
        Object isMarketingAgree = servletRequest.getSession().getAttribute("isMarketingAgree") != null;
        meService.updateInfo(request, isMarketingAgree, id);
        servletRequest.getSession().removeAttribute("isMarketingAgree");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Authentication newAuthentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), servletRequest.getSession().getAttribute("password")));;
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
//        servletRequest.getSession().removeAttribute("password");

        return ResponseEntity.ok().body(null);
    }
}
