package com.jay.shoppingmall.config.interceptors;

import com.jay.shoppingmall.security.interceptor.PasswordInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class PasswordInterceptorRegister implements WebMvcConfigurer {

    private final PasswordInterceptor passwordInterceptor;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(passwordInterceptor)
                .addPathPatterns("/me/update")
        ;
    }

}
