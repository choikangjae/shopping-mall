package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.model.page.PageDto;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.qna.QnaPageRequest;
import com.jay.shoppingmall.dto.response.QnaResponseWithPagination;
import com.jay.shoppingmall.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class PagingApiController {

    private final QnaService qnaService;

    @GetMapping("/api/v1/page/{id}/{target}")
    public ResponseEntity<?> qnaPaging(@PathVariable("id") Long itemId,
                                       @PathVariable("target") String targetPage,
                                       @CurrentUser User user,
                                       @PageableDefault(size = 5, sort = "createdDate",
                                               direction = Sort.Direction.DESC) Pageable pageable) {

        PageDto itemQnas = qnaService.getQnas(itemId, targetPage, user, pageable);

        System.out.println("itemQnas.getCustomPage().getNumber() = " + itemQnas.getCustomPage().getNumber());
        System.out.println("itemQnas.getCustomPage().getTotalPages() = " + itemQnas.getCustomPage().getTotalPages());

        return ResponseEntity.ok().body(null);
    }
    @GetMapping("/item/details/{id}/{target}")
    public ResponseEntity<?> qna(@PathVariable("id") Long itemId,
                                       @PathVariable("target") String targetPage,
                                       @CurrentUser User user,
                                       @PageableDefault(size = 5, sort = "createdDate",
                                               direction = Sort.Direction.DESC) Pageable pageable) {

        PageDto itemQnas = qnaService.getQnas(itemId, targetPage, user, pageable);

        System.out.println("itemQnas.getCustomPage().getNumber() = " + itemQnas.getCustomPage().getNumber());
        System.out.println("itemQnas.getCustomPage().getTotalPages() = " + itemQnas.getCustomPage().getTotalPages());

        return ResponseEntity.ok().body(itemQnas);
    }

//    @PostMapping
//    public ResponseEntity<?> pageApi(@Valid @RequestBody QnaPageRequest qnaPageRequest, @CurrentUser User user) {
//
//        Pageable pageable = PageRequest.of(qnaPageRequest.getRequestPage() == 0 ? 0 : qnaPageRequest.getRequestPage() - 1, qnaPageRequest.getPerPage(),Sort.by(Sort.Direction.DESC, "id"));
//
//        QnaResponseWithPagination qnaResponseWithPagination = qnaService.getQnaListByPaging(qnaPageRequest.getItemId(), user, pageable);
//
//        return ResponseEntity.ok(qnaResponseWithPagination);
//    }
}
