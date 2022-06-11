package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.IdRequest;
import com.jay.shoppingmall.dto.request.qna.QnaAnswerRequest;
import com.jay.shoppingmall.dto.request.qna.QnaWriteRequest;
import com.jay.shoppingmall.dto.response.QnaResponse;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import com.jay.shoppingmall.service.QnaService;
import com.jay.shoppingmall.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/qna")
public class QnaApiController {

    private final QnaService qnaService;
    private final SellerService sellerService;

    @PostMapping("/write")
    public ResponseEntity<?> qnaWrite(@Valid @RequestBody QnaWriteRequest qnaWriteRequest, @CurrentUser User user) {
        if (user == null) {
            throw new UserNotFoundException("잘못된 요청입니다");
        }

        QnaResponse qnaResponse = qnaService.qnaWrite(qnaWriteRequest, user);

        return ResponseEntity.ok().body(qnaResponse);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> qnaDataForUpdate(@PathVariable("id") Long qnaId, @CurrentUser User user) {
        if (user == null) {
            throw new UserNotFoundException("잘못된 요청입니다");
        }
        QnaResponse qnaResponse = qnaService.qnaFindById(qnaId);

        return ResponseEntity.ok().body(qnaResponse);
    }
    @PostMapping("/delete")
    public ResponseEntity<?> qnaDelete(@RequestBody IdRequest idRequest, @CurrentUser User user) {
        if (user == null) {
            throw new UserNotFoundException("잘못된 요청입니다");
        }
        qnaService.qnaDelete(idRequest.getId(), user);

        return ResponseEntity.ok().body(null);
    }
    @PostMapping("/answer")
    public ResponseEntity<?> qnaAnswer(@Valid @RequestBody QnaAnswerRequest qnaAnswerRequest, @CurrentUser User user) {
        if (user == null) {
            throw new UserNotFoundException("잘못된 요청입니다");
        }

        sellerService.qnaAnswer(qnaAnswerRequest, user);

        return ResponseEntity.ok().body(null);
    }
}
