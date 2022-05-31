package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.ReceiverInfoTemporarySave;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/order")
public class OrderApiController {

    @PostMapping("/receiver-info")
    public ResponseEntity<?> saveReceiverInfo(@RequestBody ReceiverInfoTemporarySave receiverInfoTemporarySave, @CurrentUser User user, HttpSession session) {
        if (user == null) {
            return ResponseEntity.badRequest().body(null);
        }

        session.setAttribute("info", receiverInfoTemporarySave);
        return ResponseEntity.ok(null);
    }

}
