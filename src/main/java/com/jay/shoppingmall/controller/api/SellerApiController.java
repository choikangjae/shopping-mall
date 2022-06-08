package com.jay.shoppingmall.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.common.model.OptionValue;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.ApiWriteItemRequest;
import com.jay.shoppingmall.dto.request.SellerAgreeRequest;
import com.jay.shoppingmall.dto.request.SellerDefaultSettingsRequest;
import com.jay.shoppingmall.exception.exceptions.AgreeException;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import com.jay.shoppingmall.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/seller")
@PreAuthorize("hasRole('SELLER')")
public class SellerApiController {

    private final SellerService sellerService;
    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/write", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> sellerItemWrite(@Valid @RequestPart ApiWriteItemRequest apiWriteItemRequest,
                                             @RequestParam("mainImage") MultipartFile file,
                                             @RequestParam(value = "descriptionImage", required = false) List<MultipartFile> files,
                                             @CurrentUser User user) {
        final List<OptionValue> optionValues = objectMapper.convertValue(apiWriteItemRequest.getOptionArray(), new TypeReference<List<OptionValue>>() {
        });
        Long itemId = sellerService.writeOptionItem(apiWriteItemRequest, optionValues, file, files, user);

        //TODO 상품 작성 이후 처리 과정 작성
        return ResponseEntity.ok(itemId);
    }

    @PostMapping("/settings")
    public ResponseEntity<?> sellerSettingsAction(@Valid @RequestBody SellerDefaultSettingsRequest request, @CurrentUser User user) {
        sellerService.sellerDefaultSettingSave(request, user);

        return ResponseEntity.ok(null);
    }

    @PostMapping("/agree")
    public ResponseEntity<?> agreeCheck(@Valid @RequestBody SellerAgreeRequest sellerAgreeRequest, @CurrentUser User user, HttpServletRequest request) {
        if (user == null) {
            throw new UserNotFoundException("잘못된 요청입니다");
        }
        Long id = user.getId();

        //TODO 데모용
        if (user.getEmail().equals("demo@user")) {
            throw new UserNotFoundException("판매자 계정을 이용해주세요..!");
        }

        if (sellerAgreeRequest.getIsSellerAgree() == null || sellerAgreeRequest.getIsLawAgree() == null) {
            throw new AgreeException("잘못된 요청입니다");
        }
        if (!sellerAgreeRequest.getIsSellerAgree() || !sellerAgreeRequest.getIsLawAgree()) {
            throw new AgreeException("필수 항목을 동의하셔야 합니다");
        }

        if (!sellerService.sellerAgreeCheck(sellerAgreeRequest, id)) {
            throw new AgreeException("잘못된 요청입니다");
        }
        Authentication oldAuthentication = SecurityContextHolder.getContext().getAuthentication();
        Authentication newAuthentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(oldAuthentication.getPrincipal(), oldAuthentication.getCredentials()));
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        return ResponseEntity.ok().body(true);
    }
}
