package com.jay.shoppingmall.config;

import com.jay.shoppingmall.security.interceptor.PasswordInterceptor;
import lombok.RequiredArgsConstructor;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.TemplateEngine;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
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
