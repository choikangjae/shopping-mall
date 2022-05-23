package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.payment.Payment;
import com.jay.shoppingmall.domain.payment.PaymentRepository;
import com.jay.shoppingmall.domain.payment.PaymentType;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.exception.exceptions.PaymentFailedException;
import com.jay.shoppingmall.exception.exceptions.SellerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SellerRepository sellerRepository;
    private final ItemRepository itemRepository;

    public Payment doPayment(final Long itemId, final PaymentType paymentType, final Integer totalPrice) {
        //외부 API 결제 로직 진행
        doSomethingWithApi();

//        Seller seller = sellerRepository.findByItemId(itemId).orElseThrow(()-> new SellerNotFoundException("해당 판매자를 찾을 수 없습니다"));
        //상품 id로 seller찾기. 다른 seller마다 다른 배송비 정책.
//        List<Item> itemList = itemRepository.findAllById(Collections.singleton(itemId));
//        List<Seller> sellerList = new ArrayList<>();
//        for (Item item : itemList) {
//            sellerRepository.findAllById(Collections.singleton(item.getSeller().getId()));
//        }

        Payment payment = Payment.builder()
                .paymentType(paymentType)
                .totalPrice(totalPrice)
//                .isShippingFeeFree(totalPrice > seller.getShippingFeePolicy())
                .build();

        return payment;
    }

    private void doSomethingWithApi() {
        if (false) {
            throw new PaymentFailedException("결제에 실패하였습니다");
        }
    }
}
