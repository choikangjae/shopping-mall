package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.qna.Qna;
import com.jay.shoppingmall.domain.qna.QnaRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.QnaWriteRequest;
import com.jay.shoppingmall.dto.response.QnaResponse;
import com.jay.shoppingmall.exception.exceptions.ItemNotFoundException;
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
                .username(user.getUsername())
                .answer(qna.getAnswer())
                .isAnswered(qna.getIsAnswered())
                .isSecret(qna.getIsSecret())
                .itemId(item.getId())
                .qnaCategory(qna.getQnaCategory())
                .qnaId(qna.getId())
                .question(qna.getQuestion())
                .isEmailNotification(qna.getIsEmailNotification())
                .build();

        return qnaResponse;
    }

//    public Page<Qna> getQnaListByPaging(Pageable pageable) {
//        Page<Qna> qnas = qnaRepository.findAllOrderByIdDesc(pageable);
//
//        return qnas;
//    }
}
