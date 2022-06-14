package com.jay.shoppingmall.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.common.model.OptionValue;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.ApiWriteItemRequest;
import com.jay.shoppingmall.dto.request.SellerAgreeRequest;
import com.jay.shoppingmall.dto.request.SellerDefaultSettingsRequest;
import com.jay.shoppingmall.dto.request.WriteItemRequest;
import com.jay.shoppingmall.exception.exceptions.AgreeException;
import com.jay.shoppingmall.exception.exceptions.NotValidException;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import com.jay.shoppingmall.service.NotificationService;
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
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    private final AuthenticationManager authenticationManager;

    @PostMapping(value = "/write", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> sellerOptionItemWrite(@Valid @RequestPart ApiWriteItemRequest apiWriteItemRequest,
                                             @RequestParam("mainImage") MultipartFile file,
                                             @RequestParam(value = "descriptionImage", required = false) List<MultipartFile> files,
                                             @CurrentUser User user) {
        if (apiWriteItemRequest.getDescription().length() > 200) {
            throw new NotValidException("설명은 200글자까지만 작성해주세요");
        }
        if (file.isEmpty()) {
            throw new NotValidException("대표 사진을 첨부해주세요");
        }
        if ((file.getSize() / (1024 * 1024)) >= 5) {
            throw new NotValidException("대표 사진 용량은 5MB를 넘을 수 없습니다");
        }
        if (files != null && files.size() > 5) {
            throw new NotValidException("상품에 대한 사진은 5장까지만 업로드가 가능합니다");
        }
        final List<OptionValue> optionValues = objectMapper.convertValue(apiWriteItemRequest.getOptionArray(), new TypeReference<List<OptionValue>>() {
        });
        Long itemId = sellerService.writeOptionItem(apiWriteItemRequest, optionValues, file, files, user);

        return ResponseEntity.ok(itemId);
    }
    @PostMapping(value = "/write-no-option", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> sellerOneItemWrite(@Valid @RequestPart WriteItemRequest writeItemRequest,
                                             @RequestParam("mainImage") MultipartFile file,
                                             @RequestParam(value = "descriptionImage", required = false) List<MultipartFile> files,
                                             @CurrentUser User user) {
        if (writeItemRequest.getDescription().length() > 200) {
            throw new NotValidException("설명은 200글자까지만 작성해주세요");
        }
        if (file.isEmpty()) {
            throw new NotValidException("대표 사진을 첨부해주세요");
        }
        if ((file.getSize() / (1024 * 1024)) >= 5) {
            throw new NotValidException("대표 사진 용량은 5MB를 넘을 수 없습니다");
        }
//        if (files != null && files.size() > 5) {
//            throw new NotValidException("상품에 대한 사진은 5장까지만 업로드가 가능합니다");
//        }
        Long itemId = sellerService.writeItem(writeItemRequest, file, files, user);

        return ResponseEntity.ok(itemId);
    }

    @PostMapping("/settings")
    public ResponseEntity<?> sellerSettingsAction(@Valid @RequestBody SellerDefaultSettingsRequest request, @CurrentUser User user) {
        final String trimmedNumber = request.getContactNumber().trim().replace("-", "");
        request.setContactNumber(trimmedNumber);
        if (!request.getContactNumber().matches("^0([1|2])([0|1|6|7|8|9])?-?([0-9]{3,4})-?([0-9]{4})$")) {
            throw new NotValidException("전화번호가 형식에 맞지 않습니다");
        }
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
