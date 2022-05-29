package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.AgreeRequest;
import com.jay.shoppingmall.dto.request.SellerAgreeRequest;
import com.jay.shoppingmall.exception.exceptions.AgreeException;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import com.jay.shoppingmall.service.MeService;
import com.jay.shoppingmall.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/seller")
public class SellerApiController {

    private final SellerService sellerService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/agree")
    public ResponseEntity<?> agreeCheck(@Valid @RequestBody SellerAgreeRequest sellerAgreeRequest, @CurrentUser User user, HttpServletRequest request) {
        if (user == null) {
            throw new UserNotFoundException("잘못된 요청입니다");
        }
        Long id = user.getId();

        if (sellerAgreeRequest.getIsSellerAgree() == null || sellerAgreeRequest.getIsLawAgree() == null) {
            throw new AgreeException("잘못된 요청입니다");
        }
        if (!sellerAgreeRequest.getIsSellerAgree() || !sellerAgreeRequest.getIsLawAgree()) {
            throw new AgreeException("필수 항목을 동의하셔야 합니다");
        }

        if (!sellerService.sellerAgreeCheck(sellerAgreeRequest, id)) {
            throw new AgreeException("잘못된 요청입니다");
        }
        Authentication oldAuthentication = SecurityContextHolder.getContext().getAuthentication();
        Authentication newAuthentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(oldAuthentication.getPrincipal(), oldAuthentication.getCredentials()));
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        return ResponseEntity.ok().body(true);
    }
}
