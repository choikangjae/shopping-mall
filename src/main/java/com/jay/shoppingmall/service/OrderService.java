package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.cart.Cart;
import com.jay.shoppingmall.domain.cart.CartRepository;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.order.DeliveryStatus;
import com.jay.shoppingmall.domain.order.Order;
import com.jay.shoppingmall.domain.order.OrderRepository;
import com.jay.shoppingmall.domain.payment.Payment;
import com.jay.shoppingmall.domain.payment.PaymentRepository;
import com.jay.shoppingmall.domain.payment.PaymentType;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.PaymentRequest;
import com.jay.shoppingmall.dto.response.CartOrderResponse;
import com.jay.shoppingmall.dto.response.CartResponse;
import com.jay.shoppingmall.dto.response.ItemResponse;
import com.jay.shoppingmall.dto.response.OrderResultResponse;
import com.jay.shoppingmall.exception.exceptions.CartEmptyException;
import com.jay.shoppingmall.exception.exceptions.ItemNotFoundException;
import com.jay.shoppingmall.exception.exceptions.PaymentFailedException;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderService {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    public List<CartOrderResponse> orderProcess(User user) {

        List<Cart> cartList = cartRepository.findByUser(user).orElseThrow(() -> new UserNotFoundException("잘못된 접근입니다"));
        if (cartList.isEmpty()) {
            throw new CartEmptyException("장바구니가 비어있습니다");
        }
        List<CartOrderResponse> cartOrderResponses = new ArrayList<>();

        for (Cart cart : cartList) {
            cartOrderResponses.add(CartOrderResponse.builder()
                    .item(cart.getItem())
                    .quantity(cart.getQuantity())
                    .build());

        }
        return cartOrderResponses;
    }

    public Integer orderTotalPrice(User user) {
        int totalPrice = 0;
        List<Cart> cartList = cartRepository.findByUser(user).orElseThrow(() -> new UserNotFoundException("잘못된 접근입니다"));
        if (cartList.isEmpty()) {
            throw new CartEmptyException("장바구니가 비어있습니다");
        }
        for (Cart cart : cartList) {
            totalPrice += cart.getQuantity() * cart.getItem().getPrice();
        }
        return totalPrice;
    }

    public Integer orderTotalCount(User user) {
        int totalCount = 0;
        List<Cart> cartList = cartRepository.findByUser(user).orElseThrow(() -> new UserNotFoundException("잘못된 접근입니다"));
        if (cartList.isEmpty()) {
            throw new CartEmptyException("장바구니가 비어있습니다");
        }
        for (Cart cart : cartList) {
            totalCount += cart.getQuantity();
        }
        return totalCount;
    }

    /**
     * seller_id를 기준으로 item을 검색해서 총 비용을 확인하고 그 비용이 seller shippingfee에 부합하는지 확인은 따로하고.
     *
     */
    public OrderResultResponse doOrderPaymentProcess(PaymentRequest paymentRequest, User user) {
        //결제 완료
        Payment payment = paymentService.doPayment(paymentRequest.getItemId(), paymentRequest.getPaymentType(), paymentRequest.getTotalPrice());

        //재고 변경
        List<Cart> cartList = cartRepository.findByUser(user).orElseThrow(() -> new UserNotFoundException("잘못된 접근입니다"));
        List<Item> itemList = new ArrayList<>();

        for (Cart cart : cartList) {
            Item item = itemRepository.findById(cart.getItem().getId()).orElseThrow(() -> new ItemNotFoundException("상품이 존재하지 않습니다"));
            item.setStock(item.getStock() - paymentRequest.getItemQuantity());
//            itemRepository.save(item);
            itemList.add(item);
        }
        //결제 정보 생성
        Order order = Order.builder()
                .payment(payment)
//                .itemList(itemList)
                .deliveryStatus(payment.getPaymentType() == PaymentType.MUTONGJANG? DeliveryStatus.WAITING_FOR_PAYMENT : DeliveryStatus.PAYMENT_DONE)
                .build();

        cartRepository.deleteByItemId(paymentRequest.getItemId());
        orderRepository.saveAndFlush(order);

        List<ItemResponse> itemResponseList = new ArrayList<>();
        for (Item item : itemList) {
            itemResponseList.add(ItemResponse.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .price(item.getPrice())
//                    .image(item.getImageList())
                            .build()
            );
        }
        return OrderResultResponse.builder()
                .itemResponseList(itemResponseList)
                .totalPrice(payment.getTotalPrice())
                .paymentType(payment.getPaymentType())
                .build();
    }
}
