package com.jay.shoppingmall.config;

import com.jay.shoppingmall.security.interceptor.PasswordInterceptor;
import lombok.RequiredArgsConstructor;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final PasswordInterceptor passwordInterceptor;

    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(passwordInterceptor)
                .addPathPatterns("/me/update")
                ;
    }

    //    @Value("${resources.path}")
//    private String uploadImagePath;
//
//    @Value("${resources.uri}")
//    private String uploadImageUri;
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler(uploadImageUri +"/**")
//                .addResourceLocations("file://" + uploadImagePath);
//    }
}
