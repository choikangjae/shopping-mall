package com.jay.shoppingmall.security.interceptor;

import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.service.MeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

@Component
@RequiredArgsConstructor
public class PasswordInterceptor implements HandlerInterceptor {

//    private final MeService meService;
    private final UserRepository userRepository;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {

        if (request.getSession().getAttribute("password") == null) {
            response.sendRedirect("/me/reconfirm");
        }
        return true;
    }
}
