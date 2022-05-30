package com.jay.shoppingmall.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private String username;
    private String defaultUrl;

    public LoginSuccessHandler(final String username, final String defaultUrl) {
        this.username = username;
        this.defaultUrl = defaultUrl;
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final Authentication authentication) throws IOException, ServletException {

//        String requestUsername = request.getParameter(username);
        clearAuthenticationExceptions(request);
        resultRedirectStrategy(request, response, authentication);
    }


    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
//    private final RequestCache requestCache = new HttpSessionRequestCache();

    protected void resultRedirectStrategy(final HttpServletRequest request,
                                          final HttpServletResponse response,
                                          final Authentication authentication) throws IOException, ServletException {

        SavedRequest savedRequest = (SavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if (savedRequest != null && savedRequest.toString().contains("api")) {
            redirectStrategy.sendRedirect(request, response, defaultUrl);
        } else {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                redirectStrategy.sendRedirect(request, response, "/admin");
            } else if (request.getSession().getAttribute("requestURI") != null) {
                String requestURI = (String) request.getSession().getAttribute("requestURI");
//            System.out.println("requestURI: " + requestURI);
                redirectStrategy.sendRedirect(request, response, requestURI);
                request.getSession().removeAttribute("requestURI");
            } else if (savedRequest != null) {
                String targetUrl = savedRequest.getRedirectUrl();
//            System.out.println("targetUrl: " + targetUrl);
                redirectStrategy.sendRedirect(request, response, targetUrl);
            } else {
//            System.out.println("defaultUrl: " + defaultUrl);
                redirectStrategy.sendRedirect(request, response, defaultUrl);
            }
        }
    }

    //에러 세션 삭제
    protected void clearAuthenticationExceptions(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null)
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}
