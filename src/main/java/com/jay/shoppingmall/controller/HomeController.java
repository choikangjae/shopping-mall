package com.jay.shoppingmall.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller
public class HomeController {

    @GetMapping("/")
    public String write(Model model) {
        return "home";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }



}
