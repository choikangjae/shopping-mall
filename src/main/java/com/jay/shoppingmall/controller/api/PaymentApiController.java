package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.PaymentRequest;
import com.jay.shoppingmall.dto.response.PaymentResponse;
import com.jay.shoppingmall.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payment")
public class PaymentApiController {

    private final PaymentService paymentService;

    @PostMapping("/record")
    public ResponseEntity<?> paymentRecordGenerateBeforePg(@Valid @RequestBody PaymentRequest paymentRequest, @CurrentUser User user) {
        PaymentResponse paymentResponse = paymentService.paymentRecordGenerateBeforePg(paymentRequest, user);

        return ResponseEntity.ok(paymentResponse);
    }
}
