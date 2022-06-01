package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.QnaPageRequest;
import com.jay.shoppingmall.dto.response.QnaResponseWithPagination;
import com.jay.shoppingmall.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/pagination")
public class PagingApiController {

    private final QnaService qnaService;

    @PostMapping
    public ResponseEntity<?> pageApi(@Valid @RequestBody QnaPageRequest qnaPageRequest, @CurrentUser User user) {

        Pageable pageable = PageRequest.of(qnaPageRequest.getRequestPage() == 0 ? 0 : qnaPageRequest.getRequestPage() - 1, qnaPageRequest.getPerPage(),Sort.by(Sort.Direction.DESC, "id"));

        QnaResponseWithPagination qnaResponseWithPagination = qnaService.getQnaListByPaging(qnaPageRequest.getItemId(), user, pageable);

        return ResponseEntity.ok(qnaResponseWithPagination);
    }
}
