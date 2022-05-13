package com.jay.shoppingmall.controller.admin;

import com.jay.shoppingmall.dto.WriteItemRequest;
import com.jay.shoppingmall.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

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
    public String adminWriteAction(@Valid WriteItemRequest writeItemRequest,
                                   @RequestPart("image") List<MultipartFile> files) {
        adminService.writeItem(writeItemRequest, files);
        return "redirect:/admin";
    }

}
