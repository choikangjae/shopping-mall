package com.jay.shoppingmall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${resources.path}")
    private String uploadImagePath;

    @Value("${resources.uri}")
    private String uploadImageUri;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(uploadImageUri +"/**")
                .addResourceLocations("file://" + uploadImagePath);
    }
}
