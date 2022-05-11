package com.jay.shoppingmall.controller.admin;

import com.jay.shoppingmall.dto.WriteItemRequest;
import com.jay.shoppingmall.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public String admin() {
        return "admin/admin-home";
    }

    @GetMapping("/write")
    public String adminWrite(WriteItemRequest writeItemRequest) {
        return "admin/admin-write-item";
    }
    @PostMapping("/write")
    public String adminWriteAction(@Valid @ModelAttribute("writeItemRequest") WriteItemRequest writeItemRequest) {
        adminService.writeItem(writeItemRequest);
        return "redirect:/admin";
    }

}
