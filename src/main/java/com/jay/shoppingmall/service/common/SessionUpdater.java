package com.jay.shoppingmall.service.common;

import com.jay.shoppingmall.dto.request.UserValidationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionUpdater {

    private final AuthenticationManager authenticationManager;

    public void sessionUpdateToken(final String email, final String rawPassword) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, rawPassword));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("authentication set");
    }
}
