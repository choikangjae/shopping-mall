package com.jay.shoppingmall.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.web.client.TestRestTemplate;

public class SessionControllerTest {

    private RedisProperties.Lettuce lettuce;
    private TestRestTemplate testRestTemplate;
    private TestRestTemplate testRestTemplateWithAuth;
    private String testUrl = "http://localhost:8080/";

    @BeforeEach
    public void setUp() {
        testRestTemplate = new TestRestTemplate();
        testRestTemplateWithAuth = new TestRestTemplate("admin", "password", null);
    }
}
