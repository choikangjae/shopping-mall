package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.AgreeRequest;
import com.jay.shoppingmall.dto.request.QnaPageRequest;
import com.jay.shoppingmall.dto.response.QnaResponse;
import com.jay.shoppingmall.dto.response.QnaResponseWithPagination;
import com.jay.shoppingmall.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/pagination")
public class PagingApiController {

    private final QnaService qnaService;

    @PostMapping
    public ResponseEntity<?> pageApi(@Valid @RequestBody QnaPageRequest qnaPageRequest, @CurrentUser User user) {

        System.out.println(qnaPageRequest.getRequestPage());
        Pageable pageable = PageRequest.of(qnaPageRequest.getRequestPage(), qnaPageRequest.getPerPage(),Sort.by(Sort.Direction.DESC, "id"));
        QnaResponseWithPagination qnaResponseWithPagination = qnaService.getQnaListByPaging(qnaPageRequest.getItemId(), user, pageable);

        return ResponseEntity.ok(qnaResponseWithPagination);
    }
}
