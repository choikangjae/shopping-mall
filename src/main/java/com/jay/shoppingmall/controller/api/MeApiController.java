package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.controller.common.UpdateValidator;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.*;
import com.jay.shoppingmall.dto.request.password.PasswordChangeRequest;
import com.jay.shoppingmall.dto.response.TrackPackageResponse;
import com.jay.shoppingmall.exception.ErrorResponse;
import com.jay.shoppingmall.exception.exceptions.AgreeException;
import com.jay.shoppingmall.exception.exceptions.PasswordInvalidException;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import com.jay.shoppingmall.service.AuthService;
import com.jay.shoppingmall.service.MeService;
import com.jay.shoppingmall.service.VirtualDeliveryService;
import com.jay.shoppingmall.service.common.SessionUpdater;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
//@PreAuthorize("hasRole('USER')")
@Api(tags = "user")
public class MeApiController {

    private final MeService meService;
    private final UpdateValidator updateValidator;
    private final AuthService authService;
    private final SessionUpdater sessionUpdater;
    private final VirtualDeliveryService virtualDeliveryService;

    @ApiOperation(value = "사용자 이용 동의", notes = "서비스 이용 동의 (필수)와 마케팅 이용 동의 (선택)을 받아 처리 결과를 반환합니다.")
    @PostMapping("/privacy/agree")
    public ResponseEntity<?> agreeCheck(@Valid @RequestBody AgreeRequest agreeRequest, @CurrentUser User user, HttpServletRequest request) {
        if (user == null) {
            throw new UserNotFoundException("잘못된 요청입니다");
        }
        Long id = user.getId();

        if (!meService.agreeCheck(agreeRequest, id)) {
            throw new AgreeException("잘못된 요청입니다");
        }
        if (agreeRequest.getIsMarketingAgree()) {
            request.getSession().setAttribute("isMarketingAgree", agreeRequest.getIsMarketingAgree());

        }
        return ResponseEntity.ok().body(true);
    }

    @ApiOperation(value = "사용자 비밀번호 변경", notes = "")
    @PostMapping("/me/password-update")
    public ResponseEntity<?> passwordUpdate(@Valid @RequestBody PasswordChangeRequest passwordChangeRequest, @CurrentUser User user) {
        if (user == null) {
            throw new UserNotFoundException("잘못된 요청입니다");
        }
        if (!passwordChangeRequest.getPasswordAfter().equals(passwordChangeRequest.getRepeatPasswordAfter())) {
            throw new PasswordInvalidException("바꿀 비밀번호가 일치하지 않습니다");
        }
        //TODO 데모용.
        if (user.getEmail().equals("demo@user") || user.getEmail().equals("demo@admin") || user.getEmail().equals("demo@seller")) {
            throw new PasswordInvalidException("데모 계정은 비밀번호 변경이 불가능합니다..!");
        }
        authService.passwordChange(passwordChangeRequest, user);
        sessionUpdater.sessionUpdateToken(user.getEmail(), passwordChangeRequest.getPasswordAfter());

        return ResponseEntity.ok(null);
    }

    @ApiOperation(value = "사용자 개인정보 변경", notes = "")
    @PostMapping("/privacy/update")
    public ResponseEntity<ErrorResponse> updateMe(@Valid @RequestBody UserUpdateRequest request, BindingResult bindingResult,
                                                  @CurrentUser User user, HttpServletRequest servletRequest) {
        updateValidator.validate(request, bindingResult);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorResponse.builder()
                    .message(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage())
                    .build());
        }

        Long id = user.getId();
        meService.updateInfo(request, id);
        servletRequest.getSession().removeAttribute("isMarketingAgree");

        sessionUpdater.sessionUpdateToken(user.getEmail(), request.getPassword());

        return ResponseEntity.ok().body(null);
    }

    @ApiOperation(value = "사용자 전화번호 중복 확인", notes = "")
    @PostMapping("/phone/duplication-check")
    public ResponseEntity<?> phoneDuplicationCheck(@RequestBody Map<String, String> phoneNumberMap) {

        String phoneNumber = phoneNumberMap.get("phoneNumber");
        phoneNumber = phoneNumber.trim().replace("-", "");
        meService.duplicationCheck(phoneNumber);

        return ResponseEntity.ok(null);
    }
    @ApiOperation(value = "사용자 상품 배송 상태 확인", notes = "운송장 번호로 조회된 결과를 반환합니다. 로그인이 되어있지 않다면 운송장 번호, 익명화된 수취자 이름, 익명화된 전달자 이름만 반환됩니다.")
    @GetMapping("/track-package/{trackingNumber}")
    public ResponseEntity<?> trackMyPackages(@PathVariable("trackingNumber") String trackingNumber, @CurrentUser User user) {
        final TrackPackageResponse trackPackageResponse = virtualDeliveryService.trackMyPackages(trackingNumber, user);

        return ResponseEntity.ok(trackPackageResponse);
    }
}
