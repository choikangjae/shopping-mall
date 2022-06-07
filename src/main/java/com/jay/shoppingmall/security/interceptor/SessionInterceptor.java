package com.jay.shoppingmall.security.interceptor;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        request.getSession().removeAttribute("itemOptions");
        request.getSession().removeAttribute("itemTotalQuantity");
        request.getSession().removeAttribute("itemTotalPrice");

        return true;
    }
}
