package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.cart.Cart;
import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.order.DeliveryStatus;
import com.jay.shoppingmall.domain.order.Order;
import com.jay.shoppingmall.domain.order.OrderRepository;
import com.jay.shoppingmall.domain.order.order_item.OrderItem;
import com.jay.shoppingmall.domain.order.order_item.OrderItemRepository;
import com.jay.shoppingmall.domain.order.order_item.order_delivery.OrderDelivery;
import com.jay.shoppingmall.domain.payment.Payment;
import com.jay.shoppingmall.domain.payment.model.PayMethod;
import com.jay.shoppingmall.domain.payment.model.Pg;
import com.jay.shoppingmall.domain.payment.model.ReceiverInfo;
import com.jay.shoppingmall.domain.payment.payment_per_seller.PaymentPerSeller;
import com.jay.shoppingmall.domain.payment.payment_per_seller.PaymentPerSellerRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.virtual_delivery_company.VirtualDeliveryCompany;
import com.jay.shoppingmall.domain.virtual_delivery_company.VirtualDeliveryCompanyRepository;
import com.jay.shoppingmall.dto.response.item.ItemAndQuantityResponse;
import com.jay.shoppingmall.dto.response.order.OrderDetailResponse;
import com.jay.shoppingmall.dto.response.seller.SellerResponse;
import com.jay.shoppingmall.service.handler.FileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    OrderService orderService;
    @Mock
    CartService cartService;
    @Mock
    OrderRepository orderRepository;
    @Mock
    OrderItemRepository orderItemRepository;
    @Mock
    PaymentPerSellerRepository paymentPerSellerRepository;
    @Mock
    VirtualDeliveryCompanyRepository virtualDeliveryCompanyRepository;
    @Mock
    ImageRepository imageRepository;
    @Mock
    FileHandler fileHandler;

    User user;
    Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        user = EntityBuilder.getUser();
    }

    @Test
    void orderProcess() {
        SellerResponse sellerResponse = SellerResponse.builder().build();
        List<ItemAndQuantityResponse> itemAndQuantityResponses = new ArrayList<>();
        ItemAndQuantityResponse itemAndQuantityResponse = ItemAndQuantityResponse.builder().isSelected(true).build();
        itemAndQuantityResponses.add(itemAndQuantityResponse);
        Map<SellerResponse, List<ItemAndQuantityResponse>> map = new HashMap<>();
        map.put(sellerResponse, itemAndQuantityResponses);

        when(cartService.showCartItemsList(any())).thenReturn(map);
        final Map<SellerResponse, List<ItemAndQuantityResponse>> sellerResponseListMap = orderService.orderProcess(user);

        assertThat(sellerResponseListMap.size()).isEqualTo(1);
        assertThat(sellerResponseListMap.get(sellerResponse)).isNotEmpty();
    }

    @Test
    void showOrderDetail() {
        Payment payment = Payment.builder()
                .pg(Pg.TOSSPAY)
                .amount(1000L)
                .payMethod(PayMethod.CARD)
                .receiverInfo(ReceiverInfo.builder().build())
                .build();

        Order order = Order.builder()
                .user(user)
                .payment(payment)
                .build();

        VirtualDeliveryCompany virtualDeliveryCompany = VirtualDeliveryCompany.builder()
                .trackingNumber("Tracking Number")
                .build();

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.builder()
                .orderDelivery(OrderDelivery.builder().deliveryStatus(DeliveryStatus.DELIVERING).build())
                .build();
        orderItemList.add(orderItem);

        List<PaymentPerSeller> paymentPerSellerList = new ArrayList<>();
        PaymentPerSeller paymentPerSeller = PaymentPerSeller.builder()
                .itemTotalPricePerSeller(1000L)
                .isMoneyTransferredToSeller(false)
                .itemShippingFeePerSeller(3000)
                .itemTotalQuantityPerSeller(20)
                .build();

        paymentPerSellerList.add(paymentPerSeller);

        when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(order));
        when(orderItemRepository.findByOrderId(any())).thenReturn(orderItemList);
        when(virtualDeliveryCompanyRepository.findByOrderItemId(any())).thenReturn(Optional.ofNullable(virtualDeliveryCompany));
        when(paymentPerSellerRepository.findByPayment(any())).thenReturn(paymentPerSellerList);

        final OrderDetailResponse orderDetailResponse = orderService.showOrderDetail(0L, user);

        assertThat(orderDetailResponse).isNotNull();
    }

//    @Test
//    void showOrders() {
//        orderService.showOrders(user, pageable);
//    }
}