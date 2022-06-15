package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.model.page.CustomPage;
import com.jay.shoppingmall.domain.model.page.PageDto;
import com.jay.shoppingmall.domain.notification.model.NotificationType;
import com.jay.shoppingmall.domain.notification.qna_notification.QnaNotification;
import com.jay.shoppingmall.domain.notification.qna_notification.QnaNotificationRepository;
import com.jay.shoppingmall.domain.qna.Qna;
import com.jay.shoppingmall.domain.qna.QnaRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.request.qna.QnaWriteRequest;
import com.jay.shoppingmall.dto.response.QnaResponse;
import com.jay.shoppingmall.dto.response.QnaResponseWithPagination;
import com.jay.shoppingmall.exception.exceptions.ItemNotFoundException;
import com.jay.shoppingmall.exception.exceptions.QnaException;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import com.jay.shoppingmall.service.common.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Boolean.valueOf;

@Service
@Transactional
@RequiredArgsConstructor
public class QnaService {

    private final QnaRepository qnaRepository;
    private final ItemRepository itemRepository;
    private final QnaNotificationRepository qnaNotificationRepository;
    private final UserRepository userRepository;

    private final SellerService sellerService;
    private final CommonService commonService;

    public QnaResponse qnaFindById(final Long id) {
        Qna qna = qnaRepository.findById(id)
                .orElseThrow(() -> new QnaException("잘못된 요청입니다"));

        if (qna.getIsAnswered() != null && qna.getIsAnswered()) {
            throw new QnaException("답변된 질문은 수정하실 수 없습니다");
        }

        return QnaResponse.builder()
                .email(qna.getUser().getEmail())
                .answer(qna.getAnswer())
                .isAnswered(qna.getIsAnswered())
                .isSecret(qna.getIsSecret())
                .itemId(qna.getItem().getId())
                .qnaCategory(qna.getQnaCategory().value())
                .qnaId(qna.getId())
                .question(qna.getQuestion())
                .isEmailNotification(qna.getIsEmailNotification())
                .build();
    }

    public QnaResponse qnaWrite(final QnaWriteRequest qnaWriteRequest, User user) {
        Item item = itemRepository.findById(qnaWriteRequest.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("상품이 존재하지 않습니다"));

        Qna qna;
        if (qnaWriteRequest.getQnaId() != null) {
            qna = qnaRepository.findById(qnaWriteRequest.getQnaId())
                    .orElseThrow(() -> new QnaException("QnA가 존재하지 않습니다"));
            qna.QnaDirtyChecker(qnaWriteRequest.getQnaCategory(), qnaWriteRequest.getIsSecret(), qnaWriteRequest.getIsEmailNotification(), qnaWriteRequest.getQuestion());
        } else {
            qna = Qna.builder()
                    .question(qnaWriteRequest.getQuestion())
                    .qnaCategory(qnaWriteRequest.getQnaCategory())
                    .isSecret(qnaWriteRequest.getIsSecret())
                    .isEmailNotification(qnaWriteRequest.getIsEmailNotification())
                    .user(user)
                    .item(item)
                    .build();
            qnaRepository.save(qna);
        }
        final User receiver = userRepository.findById(item.getSeller().getUserId())
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 없습니다"));

        if (qnaNotificationRepository.findByQnaId(qna.getId()) != null) {
            qnaNotificationRepository.deleteByQnaId(qna.getId());
        }

        QnaNotification qnaNotification = QnaNotification.builder()
                .notificationType(NotificationType.QNA_TO_SELLER)
                .receiver(receiver)
                .sender(user)
                .qnaCategory(qna.getQnaCategory())
                .message(qna.getQuestion())
                .answer(qna.getAnswer())
                .qna(qna)
                .isAnswered(qna.getIsAnswered())
                .item(item)
                .answeredAt(qna.getAnsweredAt())
                .build();
        qnaNotificationRepository.save(qnaNotification);

        return QnaResponse.builder()
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
    }

    public QnaResponseWithPagination getQnaListByPaging(Long itemId, User user, Pageable pageable) {
        Page<Qna> qnas = qnaRepository.findAllByItemId(itemId, pageable);
        Boolean isSellerItem = sellerService.sellerCheck(itemId, user);

        List<QnaResponse> qnaResponses = new ArrayList<>();
        for (Qna qna : qnas) {
            boolean isQnaOwner = user != null && user.getEmail().equals(qna.getUser().getEmail());

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
            if (isSellerItem){
                qnaResponse.setIsSecret(false);
                qnaResponse.setQuestion(qna.getQuestion());
                qnaResponse.setAnswer(qna.getAnswer());
            }
            qnaResponses.add(qnaResponse);
        }

        return QnaResponseWithPagination.builder()
                .isSellerItem(isSellerItem)
                .totalElements(qnas.getTotalElements())
                .totalPages(qnas.getTotalPages() == 0 ? 1 : qnas.getTotalPages())
                .qnaResponses(qnaResponses)
                .build();
    }

    public PageDto getQnas(Long itemId, String targetPage, User user, Pageable pageable) {
        Page<Qna> qnaPage = qnaRepository.findAllByItemId(itemId, pageable);
        CustomPage customPage = new CustomPage(qnaPage, targetPage);
        List<Qna> qnas = qnaPage.getContent();
        final Boolean isSellerItem = sellerService.sellerCheck(itemId, user);

        List<QnaResponse> qnaResponses = new ArrayList<>();
        for (Qna qna : qnas) {
            boolean isQnaOwner = user != null && user.getEmail().equals(qna.getUser().getEmail());

            final String anonymousName = commonService.anonymousName(qna.getUser().getUsername());

            //비밀글일때
            QnaResponse qnaResponse = QnaResponse.builder()
                    .username(anonymousName)
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
            //판매자일때
            if (isSellerItem){
                qnaResponse.setIsSecret(false);
                qnaResponse.setQuestion(qna.getQuestion());
                qnaResponse.setAnswer(qna.getAnswer());
            }
            qnaResponses.add(qnaResponse);
        }
        return PageDto.builder()
                .content(qnaResponses)
                .customPage(customPage)
                .build();
    }

    public void qnaDelete(final Long id, final User user) {

        Qna qna = qnaRepository.findById(id)
                .orElseThrow(() -> new QnaException("해당 QnA가 존재하지 않습니다"));
        if (!Objects.equals(qna.getUser().getId(), user.getId())) {
            throw new UserNotFoundException("유효하지 않은 접근입니다");
        }
        qnaRepository.delete(qna);
    }

    //유저가 있으면 셀러에서 유저id로 찾아보고 유저가 없으면 false.
    //유저id로 찾아본 셀러와 아이템으로 찾아본 셀러의 id가 같으면 true.

}
