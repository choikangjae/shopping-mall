package com.jay.shoppingmall.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String errorMsg = "";

        if (exception instanceof InternalAuthenticationServiceException || exception instanceof BadCredentialsException) {
            errorMsg = "아이디 또는 비밀번호가 잘못 입력 되었습니다.";
        } else {
            errorMsg = "로그인에 실패하였습니다";
        }

        log.info("User login failed. username = '{}' ip = '{}'", request.getParameter("username"), request.getRemoteAddr());
        request.setAttribute("username", request.getParameter("username"));
        request.setAttribute("errorMsg", errorMsg);
        request.setAttribute("autofocus", false);
        String DEFAULT_FAILURE_URL = "/auth/login?error";
        request.getRequestDispatcher(DEFAULT_FAILURE_URL).forward(request, response);
    }
}