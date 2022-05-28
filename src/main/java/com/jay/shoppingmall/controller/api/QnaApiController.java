package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.QnaWriteRequest;
import com.jay.shoppingmall.dto.response.QnaResponse;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import com.jay.shoppingmall.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/qna")
public class QnaApiController {

    private final QnaService qnaService;

    @PostMapping("/write")
    public ResponseEntity<?> qnaWrite(@Valid @RequestBody QnaWriteRequest qnaWriteRequest, @CurrentUser User user) {
        if (user == null) {
            throw new UserNotFoundException("잘못된 요청입니다");
        }

        QnaResponse qnaResponse = qnaService.qnaWrite(qnaWriteRequest, user);

        return ResponseEntity.ok().body(qnaResponse);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> qnaDataForUpdate(@PathVariable Long id, @CurrentUser User user) {
        if (user == null) {
            throw new UserNotFoundException("잘못된 요청입니다");
        }
        QnaResponse qnaResponse = qnaService.qnaFindById(id);

        return ResponseEntity.ok().body(qnaResponse);
    }
    @PostMapping("/delete/{id}")
    public ResponseEntity<?> qnaDelete(@PathVariable Long id, @CurrentUser User user) {
        if (user == null) {
            throw new UserNotFoundException("잘못된 요청입니다");
        }
        qnaService.qnaDelete(id, user);

        return ResponseEntity.ok().body(null);
    }
}
