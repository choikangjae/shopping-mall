package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.cart.CartRepository;
import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRelation;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.item.item_option.ItemOptionRepository;
import com.jay.shoppingmall.domain.order.DeliveryStatus;
import com.jay.shoppingmall.domain.order.Order;
import com.jay.shoppingmall.domain.order.OrderRepository;
import com.jay.shoppingmall.domain.order.order_item.OrderItem;
import com.jay.shoppingmall.domain.order.order_item.OrderItemRepository;
import com.jay.shoppingmall.domain.order.order_item.order_delivery.OrderDelivery;
import com.jay.shoppingmall.domain.payment.Payment;
import com.jay.shoppingmall.domain.payment.PaymentRepository;
import com.jay.shoppingmall.domain.payment.payment_per_seller.PaymentPerSeller;
import com.jay.shoppingmall.domain.payment.payment_per_seller.PaymentPerSellerRepository;
import com.jay.shoppingmall.domain.user.User;
//import com.jay.shoppingmall.dto.response.order.OrderDetailResponse;
import com.jay.shoppingmall.domain.virtual_delivery_company.VirtualDeliveryCompany;
import com.jay.shoppingmall.domain.virtual_delivery_company.VirtualDeliveryCompanyRepository;
import com.jay.shoppingmall.dto.response.order.OrderDetailResponse;
import com.jay.shoppingmall.dto.response.order.OrderItemCommonResponse;
import com.jay.shoppingmall.dto.response.order.OrderItemResponse;
import com.jay.shoppingmall.dto.response.order.SimpleOrderResponse;
import com.jay.shoppingmall.dto.response.item.ItemAndQuantityResponse;
import com.jay.shoppingmall.dto.response.order.payment.PaymentDetailResponse;
import com.jay.shoppingmall.dto.response.order.payment.PaymentPerSellerResponse;
import com.jay.shoppingmall.dto.response.seller.SellerResponse;
import com.jay.shoppingmall.exception.exceptions.*;
import com.jay.shoppingmall.service.handler.FileHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OrderService {

    private final CartService cartService;
    private final FileHandler fileHandler;

    private final VirtualDeliveryCompanyRepository virtualDeliveryCompanyRepository;
    private final OrderRepository orderRepository;
    private final ImageRepository imageRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentPerSellerRepository paymentPerSellerRepository;

    public Map<SellerResponse, List<ItemAndQuantityResponse>> orderProcess(User user) {
        final Map<SellerResponse, List<ItemAndQuantityResponse>> responseListMapBySeller = cartService.showCartItemsList(user);

        for (SellerResponse sellerResponse : responseListMapBySeller.keySet()) {
            final List<ItemAndQuantityResponse> itemAndQuantityResponses = responseListMapBySeller.get(sellerResponse);
            final List<ItemAndQuantityResponse> selectedCarts = itemAndQuantityResponses.stream().filter(ItemAndQuantityResponse::getIsSelected).collect(Collectors.toList());

            responseListMapBySeller.put(sellerResponse, selectedCarts);
        }
        return responseListMapBySeller;
    }

    public OrderDetailResponse showOrderDetail(final Long orderId, final User user) {

        final Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("해당하는 주문이 없습니다"));

        if (!Objects.equals(user.getId(), order.getUser().getId())) {
            log.info("Session user is not equal to order user. sessionUserId = '{}', orderUserId = '{}'", user.getId(), order.getUser().getId());
            throw new OrderNotFoundException("잘못된 접근입니다");
        }
        LocalDateTime orderDate = order.getCreatedDate();

        //상품조회
        final List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {

            boolean isTrackingStarted = false;
            String trackingNumber = "";
            final DeliveryStatus deliveryStatus = orderItem.getOrderDelivery().getDeliveryStatus();

            if (deliveryStatus.equals(DeliveryStatus.DELIVERING) || deliveryStatus.equals(DeliveryStatus.SHIPPED) || deliveryStatus.equals(DeliveryStatus.DELIVERED) || deliveryStatus.equals(DeliveryStatus.FINISHED)) {
                trackingNumber = virtualDeliveryCompanyRepository.findByOrderItemId(orderItem.getId())
                        .orElseThrow(() -> new UserNotFoundException("잘못된 접근입니다")).getTrackingNumber();

                isTrackingStarted = true;
            }
            boolean isDelivered = deliveryStatus.equals(DeliveryStatus.DELIVERED);

            final Image image = imageRepository.findByImageRelationAndId(ImageRelation.ITEM_MAIN, orderItem.getMainImageId());
            final String mainImage = fileHandler.getStringImage(image);

            OrderItemResponse orderItemResponse = OrderItemResponse.builder()
                    .orderDate(orderDate)
                    .itemName(orderItem.getItemName())
                    .option1(orderItem.getItemOption1())
                    .option2(orderItem.getItemOption2())
                    .mainImage(mainImage)
                    .sellerCompanyName(orderItem.getSellerCompanyName())
                    .orderItemId(orderItem.getId())
                    .itemPrice(orderItem.getPriceAtPurchase())
                    .quantity(orderItem.getQuantity())
                    .deliveryStatus(deliveryStatus.getValue())
                    .trackingNumber(trackingNumber)
                    .isTrackingStarted(isTrackingStarted)
                    .isDelivered(isDelivered)
                    .build();
            orderItemResponses.add(orderItemResponse);
        }

        //판매자별 분리된 결제 정보 조회
        final List<PaymentPerSeller> paymentPerSellers = paymentPerSellerRepository.findByPayment(order.getPayment());
        List<PaymentPerSellerResponse> paymentPerSellerResponses = new ArrayList<>();

        long paymentTotalPrice = 0L;
        int paymentTotalShippingFee = 0;
        for (PaymentPerSeller paymentPerSeller : paymentPerSellers) {
            final PaymentPerSellerResponse paymentPerSellerResponse = PaymentPerSellerResponse.builder()
                    .paymentPerSellerId(paymentPerSeller.getId())
                    .itemTotalPricePerSeller(paymentPerSeller.getItemTotalPricePerSeller())
                    .itemTotalQuantityPerSeller(paymentPerSeller.getItemTotalQuantityPerSeller())
                    .itemShippingFeePerSeller(paymentPerSeller.getItemShippingFeePerSeller())
                    .build();
            paymentTotalPrice += paymentPerSeller.getItemTotalPricePerSeller();
            paymentTotalShippingFee += paymentPerSeller.getItemShippingFeePerSeller();

            paymentPerSellerResponses.add(paymentPerSellerResponse);
        }
        final Payment payment = order.getPayment();

        PaymentDetailResponse paymentDetailResponse = PaymentDetailResponse.builder()
                .pg(payment.getPg().getName())
                .payMethod(payment.getPayMethod().getName())
                .paymentTotalPrice(paymentTotalPrice)
                .paymentTotalShippingFee(paymentTotalShippingFee)
                .buyerName(payment.getBuyerName())
                .buyerAddr("(" + payment.getBuyerPostcode() + ") " + payment.getBuyerAddr())
                .buyerEmail(payment.getBuyerEmail())
                .buyerTel(payment.getBuyerTel())
                .receiverName(payment.getReceiverInfo().getReceiverName())
                .receiverAddress("(" + payment.getReceiverInfo().getReceiverPostcode() + ") " + payment.getReceiverInfo().getReceiverAddress())
                .receiverEmail(payment.getReceiverInfo().getReceiverEmail())
                .receiverPhoneNumber(payment.getReceiverInfo().getReceiverPhoneNumber())
                .build();

        return OrderDetailResponse.builder()
                .orderItemResponses(orderItemResponses)
                .paymentDetailResponse(paymentDetailResponse)
                .paymentPerSellerResponses(paymentPerSellerResponses)
                .build();
    }

    public List<SimpleOrderResponse> showOrders(User user, Pageable pageable) {

        final Page<Order> orders = orderRepository.findByUserId(user.getId(), pageable)
                .orElseThrow(() -> new OrderNotFoundException("주문 정보 찾을 수 없음"));
        List<SimpleOrderResponse> simpleOrderResponses = new ArrayList<>();
        for (Order order : orders) {
            Payment payment = order.getPayment();
            if (!payment.getIsValidated()) {
                throw new PaymentFailedException("결제가 완료되지 않은 내역입니다");
            }
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());

            //주문 상품 중 가장 배송 상태가 빠른 상품을 메인 배송 상태로.
            final List<String> deliveryStatuses = orderItems.stream().map(OrderItem::getOrderDelivery).map(OrderDelivery::getDeliveryStatus).map(DeliveryStatus::getValue).distinct().collect(Collectors.toList());
            final OrderItem mostExpensiveOneAtOrder = orderItems.stream().max(Comparator.comparingLong(OrderItem::getPriceAtPurchase))
                    .orElseThrow(() -> new ItemNotFoundException("상품이 존재하지 않습니다"));

            String mainImage = fileHandler.getStringImage(imageRepository.findByImageRelationAndId(ImageRelation.ITEM_MAIN, mostExpensiveOneAtOrder.getMainImageId()));

            simpleOrderResponses.add(SimpleOrderResponse.builder()
                    .id(order.getId())
                    .orderDate(order.getCreatedDate())
                    .mainImage(mainImage)
                    .amount(payment.getAmount())
                    .deliveryStatuses(deliveryStatuses)
                    .merchantUid(payment.getMerchantUid())
                    .name(payment.getName())
                    .build());
        }
        return simpleOrderResponses;
    }
}
