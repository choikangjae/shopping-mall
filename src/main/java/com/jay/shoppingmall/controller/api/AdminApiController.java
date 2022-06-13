package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.dto.request.admin.category.CategoryAddRequest;
import com.jay.shoppingmall.dto.response.admin.category.CategoryResponse;
import com.jay.shoppingmall.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminApiController {

    private final AdminService adminService;

    @PostMapping("/category/add")
    public ResponseEntity<?> categoryAdd(@RequestBody CategoryAddRequest request) {
        final CategoryResponse categoryResponse = adminService.categoryAppend(request);

        return ResponseEntity.ok(categoryResponse);
    }
    @GetMapping("/category")
    public ResponseEntity<?> categoryAdd() {
        final List<CategoryResponse> categoryResponses = adminService.getCategoryByParent();

        return ResponseEntity.ok(categoryResponses);
    }

    }
