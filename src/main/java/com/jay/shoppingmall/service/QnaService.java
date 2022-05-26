package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.qna.Qna;
import com.jay.shoppingmall.domain.qna.QnaRepository;
import com.jay.shoppingmall.dto.request.QnaWriteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class QnaService {

    private final QnaRepository qnaRepository;

    public void qnaWrite(final QnaWriteRequest qnaWriteRequest) {
        Qna qna = Qna.builder()
                .question(qnaWriteRequest.getQuestion())
                .qnaCategory(qnaWriteRequest.getQnaCategory())
                .isSecret(qnaWriteRequest.getIsSecret())
                .isEmailNotification(qnaWriteRequest.getIsEmailNotification())
                .build();
        qnaRepository.save(qna);
    }

//    public Page<Qna> getQnaListByPaging(Pageable pageable) {
//        Page<Qna> qnas = qnaRepository.findAllOrderByIdDesc(pageable);
//
//        return qnas;
//    }
}
