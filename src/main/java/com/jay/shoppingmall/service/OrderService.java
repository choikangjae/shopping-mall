package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.cart.Cart;
import com.jay.shoppingmall.domain.cart.CartRepository;
import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.order.Order;
import com.jay.shoppingmall.domain.order.OrderRepository;
import com.jay.shoppingmall.domain.order.order_item.OrderItem;
import com.jay.shoppingmall.domain.order.order_item.OrderItemRepository;
import com.jay.shoppingmall.domain.payment.Payment;
import com.jay.shoppingmall.domain.payment.PaymentRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.response.SimpleOrderResponse;
import com.jay.shoppingmall.dto.response.cart.CartOrderResponse;
import com.jay.shoppingmall.dto.response.item.ItemResponse;
import com.jay.shoppingmall.exception.exceptions.*;
import com.jay.shoppingmall.service.handler.FileHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
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
    private final FileHandler fileHandler;
    private final ImageRepository imageRepository;
    private final OrderItemRepository orderItemRepository;

    public CartOrderResponse orderProcess(User user) {
        List<Cart> cartList = cartRepository.findByUser(user).orElseThrow(() -> new UserNotFoundException("잘못된 접근입니다"));
        if (cartList.isEmpty()) {
            throw new CartEmptyException("장바구니가 비어있습니다");
        }

        int totalPrice = 0;
        int totalCount = 0;

        List<ItemResponse> itemResponses = new ArrayList<>();
        for (Cart cart : cartList) {
            Item item = cart.getItem();
            totalPrice += cart.getQuantity() * cart.getItem().getPrice();
            totalCount += cart.getQuantity();

            itemResponses.add(ItemResponse.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .zzim(item.getZzim())
                    .cartQuantity(cart.getQuantity())
                    .mainImage(fileHandler.getStringImage(imageRepository.findByItemIdAndIsMainImageTrue(item.getId())))
                    .price(item.getPrice())
                    .salePrice(item.getSalePrice())
                    .build());
        }

        return CartOrderResponse.builder()
                .orderTotalPrice(totalPrice)
                .orderTotalCount(totalCount)
                .itemResponses(itemResponses)
                .build();
    }//todo 받는 사람 정보 올리기, QNA 고치기
    //payment 불러오고 배송상태, 주문내역(상품명 name 생성 전략 이걸로 하면 될듯)
    //order가 모든걸 총괄한다.
    public List<SimpleOrderResponse> showOrders(User user, Pageable pageable) {
        final Page<Order> orders = orderRepository.findByUserId(user.getId(), pageable)
                .orElseThrow(() -> new OrderNotFoundException("주문 정보 찾을 수 없음"));
        //todo pageable 처리 나중에 할것
        List<SimpleOrderResponse> simpleOrderResponses = new ArrayList<>();
        for (Order order : orders) {
            Payment payment = order.getPayment();
            if (!payment.getIsValidated()) {
                throw new PaymentFailedException("결제가 완료되지 않은 내역입니다");
            }
            //대표 사진
            OrderItem orderItem = orderItemRepository.findByOrderId(order.getId());
            Image image = imageRepository.findById(orderItem.getMainImageId())
                    .orElseThrow(() -> new ImageNotFoundException("사진을 찾을 수 없습니다"));
            //            Long mostExpensiveItemId = items.stream().max(Comparator.comparingLong(Item::getPrice)).map(Item::getId)
//                    .orElseThrow(()-> new ItemNotFoundException("상품이 존재하지 않습니다"));
//            String mainOfMainImage = fileHandler.getStringImage(imageRepository.findByItemIdAndIsMainImageTrue(mostExpensiveItemId));

            simpleOrderResponses.add(SimpleOrderResponse.builder()
                    .id(order.getId())
                    .orderDate(order.getCreatedDate())
                    .mainImage(fileHandler.getStringImage(image))
                    .amount(payment.getAmount())
                    .deliveryStatus(order.getDeliveryStatus().value())
                    .merchantUid(payment.getMerchantUid())
                    .name(payment.getName())
                    .build());
        }
        return simpleOrderResponses;
    }

    public void showOrderDetail(final Long orderId, final User user) {

    }

    /**
     * seller_id를 기준으로 item을 검색해서 총 비용을 확인하고 그 비용이 seller shippingfee에 부합하는지 확인은 따로하고.
     *
     */
//    public OrderResultResponse doOrderPaymentProcess(PaymentRequest paymentRequest, User user) {
//        CartOrderResponse cartOrderResponse= orderProcess(user);

        //결제 완료
//        Payment payment = paymentService.doPayment(paymentRequest.getItemId(), paymentRequest.getPg(), paymentRequest.getAmount());
//
//        //재고 변경
//        List<Cart> cartList = cartRepository.findByUser(user).orElseThrow(() -> new UserNotFoundException("잘못된 접근입니다"));
//        List<Item> itemList = new ArrayList<>();
//
//        for (Cart cart : cartList) {
//            Item item = itemRepository.findById(cart.getItem().getId()).orElseThrow(() -> new ItemNotFoundException("상품이 존재하지 않습니다"));
//            item.setStock(item.getStock() - paymentRequest.getItemQuantity());
////            itemRepository.save(item);
//            itemList.add(item);
//        }
//        //결제 정보 생성
//        Order order = Order.builder()
//                .payment(payment)
////                .itemList(itemList)
//                .deliveryStatus(payment.getPg() == Pg.MUTONGJANG? DeliveryStatus.WAITING_FOR_PAYMENT : DeliveryStatus.PAYMENT_DONE)
//                .build();
//
//        cartRepository.deleteByItemId(paymentRequest.getItemId());
//        orderRepository.saveAndFlush(order);

//        List<ItemResponse> itemResponseList = new ArrayList<>();
//        for (Item item : itemList) {
//            itemResponseList.add(ItemResponse.builder()
//                    .id(item.getId())
//                    .name(item.getName())
//                    .price(item.getPrice())
////                    .image(item.getImageList())
//                            .build()
//            );
//        }
//        return OrderResultResponse.builder()
//                .itemResponseList(itemResponseList)
//                .amount(payment.getAmount())
//                .pg(payment.getPg())
//                .build();
//    }
}
