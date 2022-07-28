package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.ReviewWriteRequest;
import com.jay.shoppingmall.dto.response.review.OrderItemForReviewResponse;
import com.jay.shoppingmall.dto.response.review.ReviewResponse;
import com.jay.shoppingmall.exception.exceptions.ReviewException;
import com.jay.shoppingmall.service.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "review")
public class ReviewApiController {

    private final ReviewService reviewService;

    @ApiOperation(value = "상품 리뷰 작성", notes = "")
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
    @ApiOperation(value = "리뷰 작성을 위한 상품 정보 조회", notes = "리뷰 작성 가능 여부를 확인하고 리뷰를 작성하면 얻을 수 있는 포인트를 계산해서 반환합니다.")
    @GetMapping("/write/{orderItemId}")
    public ResponseEntity<?> reviewWrite(@PathVariable Long orderItemId, @CurrentUser User user) {
        final OrderItemForReviewResponse orderItemForReviewResponse = reviewService.orderItemRequestForReview(orderItemId, user);

        return ResponseEntity.ok(orderItemForReviewResponse);
    }
}
