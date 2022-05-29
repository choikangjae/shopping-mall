package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.WriteItemRequest;
import com.jay.shoppingmall.dto.response.item.ItemResponse;
import com.jay.shoppingmall.dto.response.user.UserDetailResponse;
import com.jay.shoppingmall.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
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

    @GetMapping("/management/users")
    public String showUsers(Pageable pageable, Model model) {
        List<UserDetailResponse> userDetailResponses = adminService.showUserList(pageable);

        model.addAttribute("users", userDetailResponses);
        return "admin/admin-users";
    }
    @GetMapping("/management/sellers")
    public String showSellers() {

        return "admin/admin-sellers";
    }
    @GetMapping("/search")
    public String searchUsersByEmail(@RequestParam(value = "q", required = false) String email, Model model) {
        if (email == null || email.equals("")) {
            return "redirect:/admin";
        }
        final List<UserDetailResponse> userDetailResponses = adminService.searchUsersByEmail(email);
        model.addAttribute("users", userDetailResponses);
        model.addAttribute("result", email);
        return "/admin/search-result";
    }

}
