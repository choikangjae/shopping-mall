package com.jay.shoppingmall.controller.api;

import com.google.common.base.Splitter;
import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.PaymentRequest;
import com.jay.shoppingmall.dto.response.order.payment.PaymentResponse;
import com.jay.shoppingmall.dto.response.order.payment.PaymentDetailResponse;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import com.jay.shoppingmall.service.PaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payment")
@Api(tags = "payment")
public class PaymentApiController {

    private final PaymentService paymentService;

    @ApiOperation(value = "결제 이전 결제 정보 저장", notes = "결제 버튼을 누르면 요청 정보가 저장되고 결제가 되어야 할 총액과 unique한 결제 번호를 반환합니다.")
    @PostMapping("/record")
    public ResponseEntity<?> paymentRecordGenerateBeforePg(@Valid @RequestBody PaymentRequest paymentRequest, @CurrentUser User user) {
        PaymentResponse paymentResponse = paymentService.paymentRecordGenerateBeforePg(paymentRequest, user);

        return ResponseEntity.ok(paymentResponse);
    }

    @ApiOperation(value = "결제 완료", notes = "외부 PG 사에 의해 결제된 정보와 DB에 저장된 결제되어야 할 정보간의 유효성 검증 이후에 결제된 결과를 반환합니다.")
    @PostMapping("/complete")
    public ResponseEntity<?> paymentResult(@RequestBody String uid, @CurrentUser User user) throws JSONException, IOException {
        if (user == null) {
            throw new UserNotFoundException("잘못된 접근입니다");
        }
        Map<String,String> queryParameters = Splitter
                .on("&")
                .withKeyValueSeparator("=")
                .split(uid);

        String imp_uid = queryParameters.get("imp_uid");
        String merchant_uid = queryParameters.get("merchant_uid");

        PaymentDetailResponse paymentDetailResponse = paymentService.paymentTotal(imp_uid, merchant_uid, user);

        return ResponseEntity.ok(paymentDetailResponse);
    }

}
