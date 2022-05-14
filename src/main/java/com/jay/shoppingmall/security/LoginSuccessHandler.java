package com.jay.shoppingmall.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final Authentication authentication) throws IOException, ServletException {


        String url = "/";


        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        //강제로 인터셉트 당했을 때의 정보
        RequestCache requestCache = new HttpSessionRequestCache();
        System.out.println(requestCache.toString());
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        System.out.println(savedRequest);

        //직접 로그인 버튼을 눌렀을 때의 정보
        String prevPage = (String) request.getSession().getAttribute("prevPage");
        if (savedRequest.equals("/auth/null")) {
            response.sendRedirect("/me/cart");
        } else if (prevPage != null) {
            response.sendRedirect(prevPage);
        }
        else {
            response.sendRedirect(url);
        }
//
//        if (prevPage != null) {
//            request.getSession().removeAttribute("prevPage");
//        }
//
//        if (savedRequest != null) {
//            url = savedRequest.getRedirectUrl();
//            redirectStrategy.sendRedirect(request, response, url);
//
//            requestCache.removeRequest(request, response);
//            System.out.println(url);
//
//        } else if (prevPage != null && !prevPage.equals("")){
//            redirectStrategy.sendRedirect(request, response, prevPage);
//        } else {
//            redirectStrategy.sendRedirect(request, response, url);
//        }
    }
}
