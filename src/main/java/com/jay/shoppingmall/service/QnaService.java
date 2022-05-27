package com.jay.shoppingmall.service;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.qna.Qna;
import com.jay.shoppingmall.domain.qna.QnaRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.QnaWriteRequest;
import com.jay.shoppingmall.dto.response.QnaResponse;
import com.jay.shoppingmall.dto.response.QnaResponseWithPagination;
import com.jay.shoppingmall.exception.exceptions.ItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class QnaService {

    private final QnaRepository qnaRepository;
    private final ItemRepository itemRepository;

    public QnaResponse qnaWrite(final QnaWriteRequest qnaWriteRequest, User user) {
        Item item = itemRepository.findById(qnaWriteRequest.getItemId())
                .orElseThrow(()-> new ItemNotFoundException("상품이 존재하지 않습니다"));

        Qna qna = Qna.builder()
                .question(qnaWriteRequest.getQuestion())
                .qnaCategory(qnaWriteRequest.getQnaCategory())
                .isSecret(qnaWriteRequest.getIsSecret())
                .isEmailNotification(qnaWriteRequest.getIsEmailNotification())
                .user(user)
                .item(item)
                .build();
        qnaRepository.save(qna);

        QnaResponse qnaResponse = QnaResponse.builder()
                .username(qna.getUser().getUsername())
                .answer(qna.getAnswer())
                .isAnswered(qna.getIsAnswered())
                .isSecret(qna.getIsSecret())
                .itemId(item.getId())
                .qnaCategory(qna.getQnaCategory().value())
                .qnaId(qna.getId())
                .question(qna.getQuestion())
                .isEmailNotification(qna.getIsEmailNotification())
                .build();

        return qnaResponse;
    }

    public QnaResponseWithPagination getQnaListByPaging(Long id, User user, Pageable pageable) {
        Page<Qna> qnas = qnaRepository.findAllByItemId(id, pageable);
//        if (qnas.isEmpty()) {
//            throw new ItemNotFoundException("해당 상품이 존재하지 않습니다");
//        }
        List<QnaResponse> qnaResponses = new ArrayList<>();
        for (Qna qna : qnas) {
            Boolean isQnaOwner = false;
            if (user != null && user.getEmail().equals(qna.getUser().getEmail())) {
                isQnaOwner = true;
            }

            StringBuilder username = new StringBuilder(qna.getUser().getUsername());
            username.delete(username.length() / 2, username.length());
            username.append("*".repeat(username.length() / 2 + 1));

            //비밀글일때
            QnaResponse qnaResponse = QnaResponse.builder()
                    .username(String.valueOf(username))
                    .isAnswered(qna.getIsAnswered())
                    .isSecret(qna.getIsSecret())
                    .itemId(qna.getItem().getId())
                    .qnaCategory(qna.getQnaCategory().value())
                    .qnaId(qna.getId())
                    .answer(qna.getIsSecret() ? "" : qna.getAnswer())
                    .question(qna.getIsSecret() ? "" : qna.getQuestion())
                    .isEmailNotification(qna.getIsEmailNotification())
                    .createdDate(qna.getCreatedDate())
                    .isQnaOwner(isQnaOwner)
                    .build();
            //주인일때
            if (isQnaOwner) {
                qnaResponse.setQuestion(qna.getQuestion());
                qnaResponse.setAnswer(qna.getAnswer());
                qnaResponse.setIsQnaOwner(isQnaOwner);
            }
            qnaResponses.add(qnaResponse);
        }

        QnaResponseWithPagination qnaResponseWithPagination = QnaResponseWithPagination.builder()
                .totalElements(qnas.getTotalElements())
                .totalPages(qnas.getTotalPages())
                .qnaResponses(qnaResponses)
                .build();

        return qnaResponseWithPagination;
    }
}
