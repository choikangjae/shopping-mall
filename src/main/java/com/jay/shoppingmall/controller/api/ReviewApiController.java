package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.ApiWriteItemRequest;
import com.jay.shoppingmall.dto.request.ReviewWriteRequest;
import com.jay.shoppingmall.dto.response.review.ReviewAvailableCheckResponse;
import com.jay.shoppingmall.dto.response.review.ReviewResponse;
import com.jay.shoppingmall.exception.exceptions.ReviewException;
import com.jay.shoppingmall.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/review")
@PreAuthorize("hasRole('USER')")
public class ReviewApiController {

    private final ReviewService reviewService;

    @PostMapping(value = "/write", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> reviewWriteAction(@Valid @RequestPart ReviewWriteRequest request,
                                               @RequestParam(value = "reviewImages", required = false)
                                                       List<MultipartFile> files, @CurrentUser User user) {
        if (request.getText().length() < 20) {
            throw new ReviewException("20글자 이상 작성해주세요");
        }
        final ReviewResponse reviewResponse = reviewService.reviewWrite(request, user, files);

        return ResponseEntity.ok(reviewResponse);
    }
    @GetMapping("/write/{orderItemId}")
    public ResponseEntity<?> reviewWrite(@PathVariable Long orderItemId, @CurrentUser User user) {
        final ReviewAvailableCheckResponse reviewAvailableCheckResponse = reviewService.reviewWriteAvailableCheck(orderItemId, user);

        return ResponseEntity.ok(reviewAvailableCheckResponse);
    }
}
