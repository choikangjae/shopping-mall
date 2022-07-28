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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/qna")
@Api(tags = "qna")
public class QnaApiController {

    private final QnaService qnaService;
    private final SellerService sellerService;

    @ApiOperation(value = "Q&A 작성", notes = "")
    @PostMapping("/write")
    public ResponseEntity<?> qnaWrite(@Valid @RequestBody QnaWriteRequest qnaWriteRequest, @CurrentUser User user) {
        if (user == null) {
            throw new UserNotFoundException("잘못된 요청입니다");
        }

        QnaResponse qnaResponse = qnaService.qnaWrite(qnaWriteRequest, user);

        return ResponseEntity.ok().body(qnaResponse);
    }
    @ApiOperation(value = "Q&A 수정", notes = "")
    @GetMapping("/{id}")
    public ResponseEntity<?> qnaDataForUpdate(@PathVariable("id") Long qnaId, @CurrentUser User user) {
        if (user == null) {
            throw new UserNotFoundException("잘못된 요청입니다");
        }
        QnaResponse qnaResponse = qnaService.qnaFindById(qnaId);

        return ResponseEntity.ok().body(qnaResponse);
    }
    @ApiOperation(value = "Q&A 삭제", notes = "")
    @PostMapping("/delete")
    public ResponseEntity<?> qnaDelete(@RequestBody IdRequest idRequest, @CurrentUser User user) {
        if (user == null) {
            throw new UserNotFoundException("잘못된 요청입니다");
        }
        qnaService.qnaDelete(idRequest.getId(), user);

        return ResponseEntity.ok().body(null);
    }
    @ApiOperation(value = "Q&A 답변 작성", notes = "")
    @PostMapping("/answer")
    public ResponseEntity<?> qnaAnswer(@Valid @RequestBody QnaAnswerRequest qnaAnswerRequest, @CurrentUser User user) {
        if (user == null) {
            throw new UserNotFoundException("잘못된 요청입니다");
        }

        sellerService.qnaAnswerRegister(qnaAnswerRequest, user);

        return ResponseEntity.ok().body(null);
    }
}
