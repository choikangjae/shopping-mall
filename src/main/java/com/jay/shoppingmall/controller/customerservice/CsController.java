package com.jay.shoppingmall.controller.customerservice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cs")
public class CsController {

    @GetMapping
    public String cs() {
        return "cs/home";
    }
}
