package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.dto.request.admin.category.CategoryAddRequest;
import com.jay.shoppingmall.dto.response.admin.category.CategoryResponse;
import com.jay.shoppingmall.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@Api(tags = "category")
public class AdminApiController {

    private final AdminService adminService;

    @ApiOperation(value = "카테고리 추가 (관리자 권한)")
    @PostMapping("/category/add")
    public ResponseEntity<?> categoryAdd(@RequestBody CategoryAddRequest request) {
        final CategoryResponse categoryResponse = adminService.categoryAppend(request);

        return ResponseEntity.ok(categoryResponse);
    }
    @ApiOperation(value = "카테고리 목록 전체 조회 (관리자 권한)")
    @GetMapping("/category")
    public ResponseEntity<?> getCategories() {
        final List<CategoryResponse> categoryResponses = adminService.getAllRootCategories();

        return ResponseEntity.ok(categoryResponses);
    }

    }
