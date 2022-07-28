package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notification")
@Api(tags = "notification")
public class NotificationApiController {

    private final NotificationService notificationService;

    @ApiOperation(value = "알림 읽음 처리", notes = "")
    @GetMapping("/{notificationId}")
    public ResponseEntity<?> isReadDone(@PathVariable Long notificationId, @CurrentUser User user) {
        notificationService.notificationReadDone(notificationId, user);

        return ResponseEntity.ok(null);
    }

}
