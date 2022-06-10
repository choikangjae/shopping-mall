package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.response.VirtualDeliveryResponseForAnonymous;
import com.jay.shoppingmall.service.VirtualDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/delivery")
@PreAuthorize("hasRole('SELLER')")
public class VirtualDeliveryApiController {

    private final VirtualDeliveryService virtualDeliveryService;

    @PostMapping("/issue-tracking-number")
    public ResponseEntity<?> issueTrackingNumber(@RequestBody Map<String, List<Long>> map, @CurrentUser User user) {
        final List<Long> orderItemIds = map.get("orderItemIds");

        for (Long id : orderItemIds) {
            System.out.println("id = " + id);
        }
        virtualDeliveryService.issueTrackingNumber(orderItemIds, user);
        return ResponseEntity.ok(null);
//        final VirtualDeliveryResponseForAnonymous virtualDeliveryResponseForAnonymous = virtualDeliveryService.issueTrackingNumber(orderItemIds, user);
//
//        return ResponseEntity.ok(virtualDeliveryResponseForAnonymous);
    }
}
