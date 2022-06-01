package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.ItemZzimRequest;
import com.jay.shoppingmall.dto.response.ZzimResponse;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import com.jay.shoppingmall.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/item")
public class ItemApiController {

    private final ItemService itemService;

    @PostMapping("/zzim")
    public ResponseEntity<?> ItemZzim(@Valid @RequestBody ItemZzimRequest request, @CurrentUser User user) {
        if (user == null) {
            throw new UserNotFoundException("로그인이 필요한 서비스입니다");
        }

        ZzimResponse zzimResponse = itemService.itemZzim(request, user);
        return ResponseEntity.ok(zzimResponse);
    }
}
