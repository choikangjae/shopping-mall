package com.jay.shoppingmall.security.interceptor;

import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.security.UserAdapter;
import com.jay.shoppingmall.service.MeService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

@Component
@ToString
public class PasswordInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
//        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (((UserAdapter) authentication.getPrincipal()).getUser().getAgree().getIsMandatoryAgree() == null) {
//            response.sendRedirect("/me/privacy");
//        }
        if (request.getSession().getAttribute("password") == null) {
            response.sendRedirect("/me/reconfirm");
        }
        return true;
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
        request.getSession().removeAttribute("password");
    }
}
