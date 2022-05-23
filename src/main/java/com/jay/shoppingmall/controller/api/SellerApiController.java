package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.AgreeRequest;
import com.jay.shoppingmall.exception.exceptions.AgreeException;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/seller")
public class SellerApiController {

//    @PostMapping("/agree")
//    public ResponseEntity<?> agreeCheck(@Valid @RequestBody AgreeRequest agreeRequest, @CurrentUser User user, HttpServletRequest request) {
//        if (user == null) {
//            throw new UserNotFoundException("잘못된 요청입니다");
//        }
//        Long id = user.getId();
//
//        if (!meService.agreeCheck(agreeRequest, id)) {
//            throw new AgreeException("잘못된 요청입니다");
//        }
//        if (agreeRequest.getIsMarketingAgree()) {
//            request.getSession().setAttribute("isMarketingAgree", "true");
//        }
//        return ResponseEntity.ok().body(true);
//    }
}
