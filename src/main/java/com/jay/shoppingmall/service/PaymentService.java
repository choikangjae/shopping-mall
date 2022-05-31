package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.cart.Cart;
import com.jay.shoppingmall.domain.cart.CartRepository;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.payment.MerchantUidGenerator;
import com.jay.shoppingmall.domain.payment.Payment;
import com.jay.shoppingmall.domain.payment.PaymentRepository;
import com.jay.shoppingmall.domain.payment.Pg;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.PaymentRequest;
import com.jay.shoppingmall.dto.response.PaymentResponse;
import com.jay.shoppingmall.exception.exceptions.PaymentFailedException;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SellerRepository sellerRepository;
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final MerchantUidGenerator merchantUidGenerator;

    public Payment doPayment(final Long itemId, final Pg pg, final Long amount) {
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
                .pg(pg)
                .amount(amount)
//                .isShippingFeeFree(totalPrice > seller.getShippingFeePolicy())
                .build();

        return payment;
    }

    private void doSomethingWithApi() {
        if (false) {
            throw new PaymentFailedException("결제에 실패하였습니다");
        }
    }

    public PaymentResponse paymentRecordGenerateBeforePg(final PaymentRequest paymentRequest, final User user) {
        List<Cart> carts = cartRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("잘못된 접근입니다"));
        String merchantUid = merchantUidGenerator.generateMerchantUid();
        long amount = 0;

        for (Cart cart : carts) {
            amount += (long) cart.getItem().getPrice() * cart.getQuantity();
        }
        //유효성 검사 로직 더 작성할 것.

        Payment payment = Payment.builder()
                .amount(amount)
                .merchantUid(merchantUid)
                .payMethod(paymentRequest.getPayMethod())
                .pg(paymentRequest.getPg())
                .buyerAddr(paymentRequest.getBuyerAddr())
                .buyerEmail(paymentRequest.getBuyerEmail())
                .buyerName(paymentRequest.getBuyerName())
                .buyerPostcode(paymentRequest.getBuyerPostcode())
                .buyerTel(paymentRequest.getBuyerTel())
                .build();

        paymentRepository.save(payment);

        return PaymentResponse.builder()
                .amount(amount)
                .merchantUid(merchantUid)
                .build();
    }
}
