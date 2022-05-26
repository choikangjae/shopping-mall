package com.jay.shoppingmall.config.interceptors;

import com.jay.shoppingmall.security.interceptor.PasswordInterceptor;
import com.jay.shoppingmall.security.interceptor.ScrollYInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class ScrollYInterceptorRegister implements WebMvcConfigurer {

    private final ScrollYInterceptor ScrollYInterceptor;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(ScrollYInterceptor)
                .addPathPatterns("/item/**")
        ;
    }
}
