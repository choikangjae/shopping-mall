package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.controller.common.UpdateValidator;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.AgreeRequest;
import com.jay.shoppingmall.dto.request.PasswordCheckRequest;
import com.jay.shoppingmall.dto.request.UserUpdateRequest;
import com.jay.shoppingmall.exception.ErrorResponse;
import com.jay.shoppingmall.exception.exceptions.AgreeException;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import com.jay.shoppingmall.service.MeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class MeApiController {

    private final MeService meService;
    private final AuthenticationManager authenticationManager;
    private final UpdateValidator updateValidator;

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
            request.getSession().setAttribute("isMarketingAgree", "true");
        }
        return ResponseEntity.ok().body(true);
    }

    @PostMapping("/reconfirm")
    public ResponseEntity<?> reConfirm(@RequestBody PasswordCheckRequest password, @CurrentUser User user, HttpServletRequest request) {
        if (password.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        if (!meService.passwordCheck(password.getPassword(), user)) {
            return ResponseEntity.badRequest().body(null);
        }
        request.getSession().setAttribute("password", password.getPassword());

        return ResponseEntity.ok().body(true);
    }

    @PostMapping("/privacy/update")
    public ResponseEntity<ErrorResponse> updateMe(@Valid @RequestBody UserUpdateRequest request, BindingResult bindingResult,
                                                  @CurrentUser User user, HttpServletRequest servletRequest) {
        updateValidator.validate(request, bindingResult);

        if (bindingResult.hasErrors()) {
            FieldError error = bindingResult.getFieldError();
            String e = error.getField();
            System.out.println(e);
            if (e.equals("phoneNumber")) {
                return ResponseEntity.badRequest().body(ErrorResponse.builder()
                        .message("전화번호가 형식에 맞지않습니다")
                        .code("INVALID_PHONE_NUMBER")
                        .build());
            } else {
                return ResponseEntity.badRequest().body(ErrorResponse.builder()
                        .message("비어있는 값이 있습니다")
                        .code("NULL_NOT_ACCEPTED")
                        .build());
            }
        }

        Long id = user.getId();
        Object isMarketingAgree = servletRequest.getSession().getAttribute("isMarketingAgree") != null;
        meService.updateInfo(request, isMarketingAgree, id);
        servletRequest.getSession().removeAttribute("isMarketingAgree");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Authentication newAuthentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), servletRequest.getSession().getAttribute("password")));;
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
//        servletRequest.getSession().removeAttribute("password");

        return ResponseEntity.ok().body(null);
    }
}
