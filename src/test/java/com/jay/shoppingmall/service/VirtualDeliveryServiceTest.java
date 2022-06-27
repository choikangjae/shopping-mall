package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.item_option.ItemOption;
import com.jay.shoppingmall.domain.order.DeliveryStatus;
import com.jay.shoppingmall.domain.order.Order;
import com.jay.shoppingmall.domain.order.order_item.OrderItem;
import com.jay.shoppingmall.domain.order.order_item.OrderItemRepository;
import com.jay.shoppingmall.domain.order.order_item.order_delivery.OrderDelivery;
import com.jay.shoppingmall.domain.payment.Payment;
import com.jay.shoppingmall.domain.payment.model.ReceiverInfo;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.virtual_delivery_company.VirtualDeliveryCompany;
import com.jay.shoppingmall.domain.virtual_delivery_company.VirtualDeliveryCompanyRepository;
import com.jay.shoppingmall.dto.response.TrackPackageResponse;
import com.jay.shoppingmall.exception.exceptions.NotValidException;
import com.jay.shoppingmall.service.common.CommonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VirtualDeliveryServiceTest {

    @InjectMocks
    VirtualDeliveryService virtualDeliveryService;
    @Mock
    OrderItemRepository orderItemRepository;
    @Mock
    SellerRepository sellerRepository;
    @Mock
    VirtualDeliveryCompanyRepository virtualDeliveryCompanyRepository;
    @Spy
    CommonService commonService;

    User user;
    User user2;
    Seller seller;
    Item item;
    ItemOption itemOption;
    final ReceiverInfo receiverInfo = ReceiverInfo.builder()
            .receiverName("수신자 이름")
            .receiverAddress("서울시 종로")
            .receiverEmail("qwerty@email.com")
            .receiverPhoneNumber("010-1234-1234")
            .receiverPostcode("12345")
            .build();
    Payment payment = Payment.builder()
            .user(user)
            .receiverInfo(receiverInfo)
            .build();
    Order order = Order.builder()
            .user(user)
            .payment(payment)
            .build();

    OrderDelivery orderDelivery = OrderDelivery.builder()
            .deliveryStatus(DeliveryStatus.DELIVERING)
            .build();

    OrderItem orderItem = OrderItem.builder()
            .id(0L)
            .priceAtPurchase(30000L)
            .quantity(5)
            .item(item)
            .itemOption(itemOption)
            .seller(seller)
            .order(order)
            .mainImageId(0L)
            .orderDelivery(orderDelivery)
            .build();


    @BeforeEach
    void setUp() {
        user = EntityBuilder.getUser();
        user2 = EntityBuilder.getUser2();
        seller = EntityBuilder.getSeller();
        item = EntityBuilder.getItem();
        itemOption = EntityBuilder.getItemOption();
    }

    @Test
    void whenOrderItemsReturnMoreThanOneOrder_ThrowException_issueTrackingNumber() {
        List<Long> orderItemIds = new ArrayList<>();
        orderItemIds.add(0L);
        orderItemIds.add(1L);

        Order order = Order.builder()
                .user(user)
                .payment(payment)
                .build();

        Order order2 = Order.builder()
                .user(user)
                .payment(payment)
                .build();
        OrderDelivery orderDelivery = OrderDelivery.builder()
                .deliveryStatus(DeliveryStatus.PAYMENT_DONE)
                .build();
        OrderDelivery orderDelivery2 = OrderDelivery.builder()
                .deliveryStatus(DeliveryStatus.PAYMENT_DONE)
                .build();

        OrderItem orderItem = OrderItem.builder()
                .id(0L)
                .priceAtPurchase(30000L)
                .quantity(5)
                .item(item)
                .itemOption(itemOption)
                .seller(seller)
                .order(order)
                .mainImageId(0L)
                .orderDelivery(orderDelivery)
                .build();

        OrderItem orderItem2 = OrderItem.builder()
                .id(1L)
                .priceAtPurchase(30000L)
                .quantity(5)
                .item(item)
                .itemOption(itemOption)
                .seller(seller)
                .order(order2)
                .mainImageId(0L)
                .orderDelivery(orderDelivery2)
                .build();


        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));
        when(orderItemRepository.findById(0L)).thenReturn(Optional.ofNullable(orderItem));
        when(orderItemRepository.findById(1L)).thenReturn(Optional.ofNullable(orderItem2));

        assertThrows(NotValidException.class, () ->  virtualDeliveryService.issueTrackingNumber(orderItemIds, user));
    }
    @Test
    void whenOrderItemsReturnOneOrder_VirtualDeliveryWillBeSaved_issueTrackingNumber() {
        List<Long> orderItemIds = new ArrayList<>();
        orderItemIds.add(0L);
        orderItemIds.add(1L);

        Order order = Order.builder()
                .user(user)
                .payment(payment)
                .build();

        OrderDelivery orderDelivery = OrderDelivery.builder()
                .deliveryStatus(DeliveryStatus.PAYMENT_DONE)
                .build();
        OrderDelivery orderDelivery2 = OrderDelivery.builder()
                .deliveryStatus(DeliveryStatus.PAYMENT_DONE)
                .build();

        OrderItem orderItem = OrderItem.builder()
                .id(0L)
                .priceAtPurchase(30000L)
                .quantity(5)
                .item(item)
                .itemOption(itemOption)
                .seller(seller)
                .order(order)
                .mainImageId(0L)
                .orderDelivery(orderDelivery)
                .build();
        OrderItem orderItem2 = OrderItem.builder()
                .id(1L)
                .priceAtPurchase(30000L)
                .quantity(5)
                .item(item)
                .itemOption(itemOption)
                .seller(seller)
                .order(order)
                .mainImageId(0L)
                .orderDelivery(orderDelivery2)
                .build();

        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));
        when(orderItemRepository.findById(0L)).thenReturn(Optional.ofNullable(orderItem));
        when(orderItemRepository.findById(1L)).thenReturn(Optional.ofNullable(orderItem2));

        virtualDeliveryService.issueTrackingNumber(orderItemIds, user);

        verify(virtualDeliveryCompanyRepository, times(2)).save(any());
    }

    @Test
    void whenSessionUserIsEqualToRequestUser_ShowFullInfo_trackMyPackages() {
        String trackingNumber = "trackingnumber";

        final VirtualDeliveryCompany virtualDeliveryCompany = VirtualDeliveryCompany.builder()
                .trackingNumber(trackingNumber)
                .packageName(order.getPayment().getName())
                .senderName(seller.getCompanyName())
                .senderAddress(seller.getItemReleaseAddress().getFullAddress())
                .senderPostcode(seller.getItemReleaseAddress().getZipcode())
                .senderPhoneNumber(seller.getContactNumber())
                .receiverName(receiverInfo.getReceiverName())
                .receiverPhoneNumber(receiverInfo.getReceiverPhoneNumber())
                .receiverAddress(receiverInfo.getReceiverAddress())
                .receiverPostcode(receiverInfo.getReceiverPostcode())
                .seller(seller)
                .user(user)
                .orderItem(orderItem)
                .build();

        when(virtualDeliveryCompanyRepository.findFirstByTrackingNumber(any())).thenReturn(Optional.ofNullable(virtualDeliveryCompany));

        final TrackPackageResponse trackPackageResponse = virtualDeliveryService.trackMyPackages(trackingNumber, user);

        assertThat(trackPackageResponse.getVirtualDeliveryResponseForAnonymous()).isNull();
        assertThat(trackPackageResponse.getVirtualDeliveryResponse()).isNotNull();
        assertThat(trackPackageResponse.getVirtualDeliveryResponse().getUserId()).isEqualTo(user.getId());
    }
    @Test
    void whenSessionUserIsNotEqualToRequestUser_ShowAnonymousInfo_trackMyPackages() {
        String trackingNumber = "trackingnumber";

        final VirtualDeliveryCompany virtualDeliveryCompany = VirtualDeliveryCompany.builder()
                .trackingNumber(trackingNumber)
                .packageName(order.getPayment().getName())
                .senderName(seller.getCompanyName())
                .senderAddress(seller.getItemReleaseAddress().getFullAddress())
                .senderPostcode(seller.getItemReleaseAddress().getZipcode())
                .senderPhoneNumber(seller.getContactNumber())
                .receiverName(receiverInfo.getReceiverName())
                .receiverPhoneNumber(receiverInfo.getReceiverPhoneNumber())
                .receiverAddress(receiverInfo.getReceiverAddress())
                .receiverPostcode(receiverInfo.getReceiverPostcode())
                .seller(seller)
                .user(user2)
                .orderItem(orderItem)
                .build();

        when(virtualDeliveryCompanyRepository.findFirstByTrackingNumber(any())).thenReturn(Optional.ofNullable(virtualDeliveryCompany));

        final TrackPackageResponse trackPackageResponse = virtualDeliveryService.trackMyPackages(trackingNumber, user);

        assertThat(trackPackageResponse.getVirtualDeliveryResponseForAnonymous()).isNotNull();
        assertThat(trackPackageResponse.getVirtualDeliveryResponse()).isNull();
    }
}